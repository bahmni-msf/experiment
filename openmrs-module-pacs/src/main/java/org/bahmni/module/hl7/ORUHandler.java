package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class ORUHandler implements ReceivingApplication {

    private static final org.apache.log4j.Logger log = Logger.getLogger(ORUHandler.class);

    @Override
    public Message processMessage(Message message, Map<String, Object> stringObjectMap) throws ReceivingApplicationException, HL7Exception {
        try {
            log.debug(message.encode());
            log.debug("--------------------");

            ORU_R01 oruR01 = (ORU_R01) message;

            String messageControlID = oruR01.getMSH().getMessageControlID().getValue();
            String sendingFacility = oruR01.getMSH().getSendingFacility().getName();
            String patientIdentifier = oruR01.getRESPONSE().getPATIENT().getPID().getPatientIDInternalID(0).getID().getValue();
            log.debug("messagecontrolid:'" + messageControlID + "'");
            log.debug("facility:'" + sendingFacility + "'");
            log.debug("Patient Id:'" + patientIdentifier + "'");

            String encodedMessage = new PipeParser().encode(message);
            log.debug("Received message:\n" + encodedMessage + "\n\n");

        } catch(Throwable t) {
            log.debug(t);
        } finally {
            try {
                return message.generateACK();
            } catch (IOException e) {
                throw new ReceivingApplicationException(e);
            }
        }
    }

    @Override
    public boolean canProcess(Message message) {
        log.debug("ORUHandler.canProcess");
        log.debug(message);
        return true;
//        return message instanceof ORU_R01;
    }
}
