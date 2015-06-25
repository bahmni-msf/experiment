package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;

public class OrderCreator {
    private static final org.apache.log4j.Logger log = Logger.getLogger(OrderCreator.class);
    private final String host;
    private final int port;
    private final int timeout;

    public OrderCreator(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9000;

        String remoteHost = "10.0.0.25";
        int remotePort = 1235;

        int timeout = 300000;

        if (args.length > 0) {
            host = args[0];
            if (args.length >= 2)
                port = Integer.parseInt(args[1]);
            if (args.length >= 3)
                timeout = Integer.parseInt(args[2]);
        }

        OrderCreator orderCreator = new OrderCreator(host, port, timeout);
        orderCreator.createRemoteOrder(remoteHost, remotePort);
    }

    private void createRemoteOrder(String remoteHost, int remotePort) throws HL7Exception, LLPException, IOException {
        //Creating client to accept Message i.e PACS server here
        Connection newClientConnection = null;
        try {
            ConnectionHub connectionHub = ConnectionHub.getInstance();
            newClientConnection = connectionHub.attach(remoteHost, remotePort, new PipeParser(), MinLowerLayerProtocol.class);
            Initiator initiator = newClientConnection.getInitiator();
            Message response = initiator.sendAndReceive(createRadiologyOrderMessage());
            String responseString = new PipeParser().encode(response);

            log.info("Received response:\n" + responseString);
        } finally {
            if (newClientConnection != null) newClientConnection.close();
        }
    }

    public static ORM_O01 createRadiologyOrderMessage() throws HL7Exception {
        ORM_O01 message = new ORM_O01();

        // handle the MSH component
        MSH msh = message.getMSH();
        msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_1");
        HL7Utils.populateMessageHeader(msh, new Date(), "ORM", "O01", "Bahmni EMR");

        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
//        pid.getPatientID().getIDNumber().setValue("GAN00001");
        pid.getPatientIdentifierList(0).getIDNumber().setValue("GAN00001");
        pid.getPatientName(0).getFamilyName().getSurname().setValue("Patient");
        pid.getPatientName(0).getGivenName().setValue("Dummy1");
        pid.getDateTimeOfBirth().getTime().setValue("20120830");
        pid.getAdministrativeSex().setValue("M");
        // TODO: do we need patient admission ID / account number

        patient.getNTE(0).insertComment(0).setValue("Order Filler Comment/Notes");
        patient.getNTE(0).getSourceOfComment().setValue("L");
        patient.getNTE(0).getSetIDNTE().setValue("0");

        patient.getNTE(1).insertComment(0).setValue("Order Placer Comment/Notes");
        patient.getNTE(1).getSourceOfComment().setValue("P");
        patient.getNTE(1).getSetIDNTE().setValue("1");

        // handle patient visit component
        PV1 pv1 = patient.getPATIENT_VISIT().getPV1();
        pv1.getAssignedPatientLocation().getPointOfCare().setValue("OPD");
        pv1.getAssignedPatientLocation().getPersonLocationType().setValue("EMR");
        pv1.getReferringDoctor(0).getIDNumber().setValue("1");
        pv1.getReferringDoctor(0).getFamilyName().getSurname().setValue("Dummy");
        pv1.getReferringDoctor(0).getGivenName().setValue("Doctor");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue("A00");
        orc.getFillerOrderNumber().getEntityIdentifier().setValue("B00"); // Accession number in Imagesuite
        orc.getEnteredBy(0).getGivenName().setValue("Bahmni");
        orc.getOrderControl().setValue("NW");
        orc.getOrderingProvider(0).getGivenName().setValue("OrderingProvider");


        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR(); // http://www.mexi.be/documents/hl7/ch400024.htm  http://www.mexi.be/documents/hl7/ch700010.htm
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("1234");
        obr.getUniversalServiceIdentifier().getText().setValue("Abdomen ap");
        //obr.getUniversalServiceIdentifier().getNameOfCodingSystem().setValue("");
        obr.getProcedureCode().getIdentifier().setValue("1234");

//        obr.getFillerOrderNumber().getEntityIdentifier().setValue("ORNO1");

        // note that we are just sending modality here, not the device location
        obr.getPlacerField2().setValue("CR");
        obr.getQuantityTiming(0).getPriority().setValue("STAT");
        obr.getScheduledDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(new Date()));

        // break the reason for study up by lines
        obr.getReasonForStudy(0).getText().setValue("Creating a test order programmatically");
        obr.getReasonForStudy(1).getText().setValue("This is a test order. Please ignore this order.");

        obr.getCollectorSComment(0).getText().setValue("This is Collector's Notes : Please ignore this Order as its a test that we doing for Order creation");

        return message;
    }

    public static Message editRadiologyOrderMessage(ORM_O01 message) throws DataTypeException {
        ORC orc = message.getORDER().getORC();
        orc.getOrderControl().setValue("XO");


        return message;
    }

    public static Message changeStatusRadiologyOrderMessage(ORM_O01 message) throws DataTypeException {
        ORC orc = message.getORDER().getORC(); //http://www.mexi.be/documents/hl7/ch400009.htm
        orc.getOrderControl().setValue("SC");
        orc.getOrderStatus().setValue("CM"); //CM Order is complete
        return message;
    }

    public static Message cancelRadiologyOrderMessage(ORM_O01 message) throws DataTypeException {
        ORC orc = message.getORDER().getORC();
        orc.getOrderControl().setValue("CA");
        return message;
    }
}
