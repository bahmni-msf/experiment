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
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;
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

        SendAndReceiveAMessage sendAndReceiveAMessage = new SendAndReceiveAMessage(host, port, timeout);
        sendAndReceiveAMessage.startServer();
//        sendAndReceiveAMessage.createRemoteOrder(remoteHost, remotePort);
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
            Message response = initiator.sendAndReceive(createRadiologyOrderMessage());
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
        PID pid = message.getPATIENT().getPID();
        pid.getPatientID().getIDNumber().setValue("GAN00001");
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
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue("A00");
        orc.getFillerOrderNumber().getEntityIdentifier().setValue("B00");
        orc.getEnteredBy(0).getGivenName().setValue("Bahmni");
        orc.getOrderControl().setValue("NW");

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("Chest lordotic xray");
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

}