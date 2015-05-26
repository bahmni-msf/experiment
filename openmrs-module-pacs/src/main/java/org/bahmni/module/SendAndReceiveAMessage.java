package org.bahmni.module;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.app.SimpleServer;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;

import java.util.Date;

public class SendAndReceiveAMessage {

    public static void main(String[] args) throws Exception {
        int port = 104;
        HL7Service server = new SimpleServer(port, new MinLowerLayerProtocol(), new PipeParser());
        Application handler = new DefaultApplication();
        server.registerApplication("ORU", "001", handler);
        server.registerApplication("ORM", "001", handler);
        server.registerApplication("*", "*", handler);

        server.registerConnectionListener(
            new ConnectionListener() {
                @Override
                public void connectionReceived(Connection connection) {
                    System.out.println("New connection received: " + connection.getRemoteAddress().toString());
                }

                @Override
                public void connectionDiscarded(Connection connection) {
                    System.out.println("Lost connection from: " + connection.getRemoteAddress().toString());
                }
        });
        server.start();

        //Creating client to accept Message i.e PACS server here
        ConnectionHub connectionHub = ConnectionHub.getInstance();
        Connection newClientConnection = connectionHub.attach("localhost",port,new PipeParser(),SendAndReceiveAMessage.class);
        Initiator initiator = newClientConnection.getInitiator();
        Message response = initiator.sendAndReceive(getORMMessage());
        System.out.print(response);
        newClientConnection.close();
        server.stop();

    }

    public static Message getORMMessage() throws HL7Exception {
        ORM_O01 message = new ORM_O01();

        // handle the MSH component
        MSH msh = message.getMSH();
        HL7Utils.populateMessageHeader(msh, new Date(), "ORM", "O01", "Bahmni EMR");

        // handle the patient PID component
        PID pid = message.getPATIENT().getPID();
        pid.getPatientIDInternalID(0).getID().setValue("GAN0'001");
        pid.getPatientName().getFamilyName().setValue("Gond");
        pid.getPatientName().getGivenName().setValue("Ramesh");
        pid.getDateOfBirth().getTimeOfAnEvent().setValue("08-08-2012");
        pid.getSex().setValue("M");
        // TODO: do we need patient admission ID / account number

        PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();

        pv1.getAssignedPatientLocation().getPointOfCare().setValue("Location");

        pv1.getAssignedPatientLocation().getLocationType().setValue("EMR");

        pv1.getReferringDoctor(0).getIDNumber().setValue("ProviderId");
        pv1.getReferringDoctor(0).getFamilyName().setValue("Sharma");
        pv1.getReferringDoctor(0).getGivenName().setValue("Provider");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getOrderControl().setValue("ordercontrol");

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getFillerOrderNumber().getEntityIdentifier().setValue("ORNO1");
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("XXX");

        obr.getUniversalServiceIdentifier().getText()
                .setValue("XX");

        // note that we are just sending modality here, not the device location
        obr.getPlacerField2().setValue("HH");
        obr.getQuantityTiming().getPriority().setValue("STAT");
        obr.getScheduledDateTime().getTimeOfAnEvent().setValue(HL7Utils.getHl7DateFormat().format("08-07-2015"));

        // break the reason for study up by lines
        obr.getReasonForStudy(0).getText().setValue("line");

        return message;
    }

    public static Message getADTMessage() throws HL7Exception {
        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01|12345|P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";
        PipeParser p = new PipeParser();
        Message adt = p.parse(msg);

        return adt;
    }
}
