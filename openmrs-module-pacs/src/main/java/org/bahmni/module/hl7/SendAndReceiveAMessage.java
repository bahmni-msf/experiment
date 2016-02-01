package org.bahmni.module.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ADT_A08;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.idgenerator.UUIDGenerator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;

public class SendAndReceiveAMessage {
    private static final org.apache.log4j.Logger log = Logger.getLogger(SendAndReceiveAMessage.class);

    private final String host;
    private final int port;
    private final int timeout;

    public SendAndReceiveAMessage(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9000;

        String remoteHost = "192.168.33.10";//"192.168.0.75";
        int remotePort = 2575;

        int timeout = 300000;

        if (args.length > 0) {
            host = args[0];
            if (args.length >= 2)
                port = Integer.parseInt(args[1]);
            if (args.length >= 3)
                timeout = Integer.parseInt(args[2]);
        }

        SendAndReceiveAMessage sendAndReceiveAMessage = new SendAndReceiveAMessage(host, port, timeout);
        sendAndReceiveAMessage.startServer();
        sendAndReceiveAMessage.createRemoteOrder(remoteHost, remotePort);
//        sendAndReceiveAMessage.createLocalOrder(host, port);
    }

    private void startServer() throws InterruptedException {
        //        HL7Service server = new SimpleServer(port, new MinLowerLayerProtocol(), new PipeParser());
        HapiContext hapiContext = new DefaultHapiContext();
        HL7Service server = hapiContext.newServer(port, false);
        server.registerApplication("ORM", "001", new ORMHandler());
        server.registerApplication("ORU", "R01", new ORUHandler());
        server.setExceptionHandler(new ErrorHandler());
        server.registerConnectionListener(
            new ConnectionListener() {
                @Override
                public void connectionReceived(Connection connection) {
                    log.debug("New connection received: " + connection.getRemoteAddress().toString());
                }

                @Override
                public void connectionDiscarded(Connection connection) {
                    log.debug("Lost connection from: " + connection.getRemoteAddress().toString());
                }
            });
        server.startAndWait();
        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", Integer.toString(timeout));

        log.debug("Started server at " + host + ":" + port + " with timeout of " + timeout);
    }

    private void createLocalOrder(String host, int port) throws HL7Exception, LLPException, IOException {
        log.debug("Sending create order message to " + host + " server at port " + port);

        HapiContext hapiContext = new DefaultHapiContext();
        Connection newClientConnection = hapiContext.newClient(host, port, false);
        Initiator initiator = newClientConnection.getInitiator();
        Message response = initiator.sendAndReceive(createRadiologyOrderMessage());
        String responseString = new PipeParser().encode(response);

        log.debug("Received response:\n" + responseString);
        newClientConnection.close();
    }

    public void createRemoteOrder(String remoteHost, int remotePort) throws HL7Exception, LLPException, IOException {
        //Creating client to accept Message i.e PACS server here
        Connection newClientConnection = null;
        try {
            ConnectionHub connectionHub = ConnectionHub.getInstance();
            newClientConnection = connectionHub.attach(remoteHost, remotePort, new PipeParser(), MinLowerLayerProtocol.class);
            Initiator initiator = newClientConnection.getInitiator();
//            Message response = initiator.sendAndReceive(createRadiologyOrderMessage());
//            Message response = initiator.sendAndReceive(createPatientEditMessage());
//            Message response = initiator.sendAndReceive(createPatientEditMessageV231());
            Message response = initiator.sendAndReceive(createGeneric_Message());
            System.out.print(response);
            String responseString = new PipeParser().encode(response);

            log.debug("Received response:\n" + responseString);
        } finally {
            if (newClientConnection != null) newClientConnection.close();
        }
    }

    public static Message createRadiologyOrderMessage() throws HL7Exception {
        ORM_O01 message = new ORM_O01();

        // handle the MSH component
        MSH msh = message.getMSH();
        msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_1");
        HL7Utils.populateMessageHeader(msh, new Date(), "ORM", "O01", "Bahmni EMR");

        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue("GAN111113");
        pid.getPatientName(0).getFamilyName().getSurname().setValue("Patient");
        pid.getPatientName(0).getGivenName().setValue("Dummy");
        pid.getDateTimeOfBirth().getTime().setValue("20120830");
        pid.getAdministrativeSex().setValue("M");
        // TODO: do we need patient admission ID / account number

        // handle patient visit component
        PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();
        pv1.getAssignedPatientLocation().getPointOfCare().setValue("OPD");
        pv1.getAssignedPatientLocation().getPersonLocationType().setValue("EMR");
        pv1.getReferringDoctor(0).getIDNumber().setValue("1");
        pv1.getReferringDoctor(0).getFamilyName().getSurname().setValue("Dummy");
        pv1.getReferringDoctor(0).getGivenName().setValue("Doctor");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue("A00111");
        orc.getFillerOrderNumber().getEntityIdentifier().setValue("B00111");
        orc.getEnteredBy(0).getGivenName().setValue("Bahmni");
        orc.getOrderControl().setValue("NW");

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("CHESTLORDOTICXRAY");
        obr.getUniversalServiceIdentifier().getText().setValue("Chest lordotic xray");
//        obr.getFillerOrderNumber().getEntityIdentifier().setValue("ORNO1");

        // note that we are just sending modality here, not the device location
        obr.getPlacerField2().setValue("CR");
        obr.getQuantityTiming(0).getPriority().setValue("STAT");
        obr.getScheduledDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(new Date()));

        // break the reason for study up by lines
        obr.getReasonForStudy(0).getText().setValue("Creating a test order programmatically");
        obr.getReasonForStudy(1).getText().setValue("This is a test order. Please ignore this order.");

        return message;
    }

    public AbstractMessage createPatientEditMessage() throws DataTypeException {
        ADT_A01 message = new ADT_A01();
        // handle the MSH component
        MSH msh = message.getMSH();
        msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_2");
        HL7Utils.populateMessageHeader(msh, new Date(), "ADT", "A08", "Bahmni EMR");

        EVN evn = message.getEVN();
        evn.getEventTypeCode().setValue("A08");
        evn.getRecordedDateTime().getTime().setValue(HL7Utils.getHl7DateFormat().format(new Date()));

        // handle the patient PID component
        PID pid = message.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue("BAH228886");
        pid.getPatientName(0).getFamilyName().getSurname().setValue("Test1");
        pid.getPatientName(0).getGivenName().setValue("patnovone1");
        pid.getDateTimeOfBirth().getTime().setValue("20120830");
        pid.getAdministrativeSex().setValue("M");

        PV1 pv1 = message.getPV1();
        pv1.getPv12_PatientClass().setValue("O");

        return message;
    }

    public AbstractMessage createPatientEditMessageV231() throws DataTypeException {
        ADT_A08 message = new ADT_A08();

        ca.uhn.hl7v2.model.v231.segment.MSH msh = message.getMSH();
        msh.getMessageControlID().setValue("MESSAGE_CONTROL_ID_2");
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingFacility().getHd1_NamespaceID().setValue("ExperimentEMR");
        msh.getSendingFacility().getUniversalID().setValue("ExperimentEMR");
        msh.getSendingFacility().getNamespaceID().setValue("ExperimentEMR");
        msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(HL7Utils.getHl7DateFormat().format(new Date()));
        msh.getMessageType().getMsg1_MessageType().setValue("ADT");
        msh.getMessageType().getTriggerEvent().setValue("A08");
        msh.getProcessingID().getProcessingID().setValue("P");
        msh.getVersionID().getVersionID().setValue("2.5");

        ca.uhn.hl7v2.model.v231.segment.EVN evn = message.getEVN();
        evn.getRecordedDateTime().getTimeOfAnEvent().setValue(HL7Utils.getHl7DateFormat().format(new Date()));
        evn.getEventTypeCode().setValue("A08");

        // handle the patient PID component
        ca.uhn.hl7v2.model.v231.segment.PID pid = message.getPID();
        pid.getPatientIdentifierList(0).getID().setValue("GAN111111");
        pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue("Pati");//FamilyName
        pid.getPatientName(0).getGivenName().setValue("Dum");//GivenName
        pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(new Date());
        pid.getSex().setValue("M");

        ca.uhn.hl7v2.model.v231.segment.PV1 pv1 = message.getPV1();
        pv1.getPv12_PatientClass().setValue("O");

        return message;
    }

    public Message createGeneric_Message() throws HL7Exception {
//        String msg = "MSH|^~\\&||Bahmni EMR^Bahmni EMR|||20151120124227||ADT^A08|MESSAGE_CONTROL_ID_2|T|2.5|\r"
//            + "EVN|A08|20151120124227|\r"
//            + "PID|||BAH228886||Test1^patnovone1||20120830|M|\r"
//            + "PV1||O\r";

        String msg = "MSH|^~\\&||BahmniEMR^BahmniEMR|||2015120210||ORM^O01|144896054989310573|P|2.5\r"
                + "PID|||BAH227661||Anita^BAH227661||19881029000000+0530|F\r"
                + "ORC|NW|ORD-106327|ORD-106327|||||||^^BahmniEMR||a0f83ce7-267e-4730-9f78-5d924beee3c1^^Gokul Kafle\r"
                + "OBR|5|5|5|KNEE-LT^Knee-LATERAL||||||||||||||AccNo7|ProcId7|SPStepId7|\r"
                + "ZDS|1.2.4.0.13.1.4.2252867.1.ORD-106326";

        String msg1 = "MSH|^~\\&||BahmniEMR^BahmniEMR|||2015120410||ORM^O01|144920444787410820|P|2.5\r"
                + "PID|||BAH149551||^BAH149551||20090101000000+0545|M\r"
                + "ORC|NW|ORD-108207|ORD-108207|||||||^^BahmniEMR||4734a18a-3175-4ba5-8859-54650f924d66^^Sameer Raj Joshi\r"
                + "OBR||||4ARM-LT^ForeARM-LATERAL|||||||||||||||||||||||||||||||||||^ForeARM-LATERAL||||^Mahendra,saud\r";

        PipeParser p = new PipeParser();
        Message adt = p.parse(msg1);
        UUIDGenerator generator = new UUIDGenerator();
//        generator.getID();
        return adt;
    }

}