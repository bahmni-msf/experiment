package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.ConnectionListener;
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
import ca.uhn.hl7v2.protocol.ReceivingApplication;

import java.util.Date;

public class SendAndReceiveAMessage {

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8042;
        int timeout = 3000;

        if (args.length > 0) {
            host = args[0];
            if (args.length >= 2)
                port = Integer.parseInt(args[1]);
            if (args.length >= 3)
                timeout = Integer.parseInt(args[2]);
        }
        System.out.println(host + ":" + port + ":" + timeout);
        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", Integer.toString(timeout));

        HL7Service server = new SimpleServer(port, new MinLowerLayerProtocol(), new PipeParser());
        ReceivingApplication serverSideOrderHandler = new ORMHandler();
        server.registerApplication("ORM", "001", serverSideOrderHandler);
        server.registerApplication("*", "*", serverSideOrderHandler);
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
        Connection newClientConnection = connectionHub.attach(host, port, new PipeParser(), MinLowerLayerProtocol.class);
        Initiator initiator = newClientConnection.getInitiator();
        Message response = initiator.sendAndReceive(createRadiologyOrderMessage());
        String responseString = new PipeParser().encode(response);

        System.out.println("Received response:\n" + responseString);
        newClientConnection.close();
        server.stop();
    }

    public static Message createRadiologyOrderMessage() throws HL7Exception {
        ORM_O01 message = new ORM_O01();

        // handle the MSH component
        MSH msh = message.getMSH();
        msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_1");
        HL7Utils.populateMessageHeader(msh, new Date(), "ORM", "O01", "Bahmni EMR");

        // handle the patient PID component
        PID pid = message.getPATIENT().getPID();
        pid.getPatientIDInternalID(0).getID().setValue("GAN00001");
        pid.getPatientName(0).getFamilyName().setValue("Patient");
        pid.getPatientName(0).getGivenName().setValue("Dummy");
        pid.getDateOfBirth().getTimeOfAnEvent().setValue("20120830");
        pid.getSex().setValue("M");
        // TODO: do we need patient admission ID / account number

        // handle patient visit component
        PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();
        pv1.getAssignedPatientLocation().getPointOfCare().setValue("OPD");
        pv1.getAssignedPatientLocation().getLocationType().setValue("EMR");
        pv1.getReferringDoctor(0).getIDNumber().setValue("1");
        pv1.getReferringDoctor(0).getFamilyName().setValue("Dummy");
        pv1.getReferringDoctor(0).getGivenName().setValue("Doctor");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getOrderControl().setValue("NW");

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getPlacerOrderNumber(0).getUniversalID().setValue("A01");
        obr.getPlacerOrderNumber(0).getNamespaceID().setValue("A01");
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("Chest lordotic xray");
        obr.getUniversalServiceIdentifier().getText().setValue("Chest lordotic xray");
        obr.getFillerOrderNumber().getEntityIdentifier().setValue("ORNO1");

        // note that we are just sending modality here, not the device location
        obr.getPlacerField2().setValue("CR");
        obr.getQuantityTiming().getPriority().setValue("STAT");
        obr.getScheduledDateTime().getTimeOfAnEvent().setValue(HL7Utils.getHl7DateFormat().format(new Date()));

        // break the reason for study up by lines
        obr.getReasonForStudy(0).getText().setValue("Creating a test order programmatically");
        obr.getReasonForStudy(1).getText().setValue("This is a test order. Please ignore this order.");

        return message;
    }

}