package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class ORMHandler implements ReceivingApplication {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ORMHandler.class);

    @Override
    public Message processMessage(Message message, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
        ORM_O01 ormMessage = (ORM_O01) message;
        String messageControlId = ormMessage.getMSH().getMessageControlID().getValue();
        String sendingFacility = ormMessage.getMSH().getSendingFacility().getName();
        log.debug("messagecontrolid:'" + messageControlId + "'");
        log.debug("facility:'" + sendingFacility + "'");

        String encodedMessage = new PipeParser().encode(message);
        log.debug("Received message:\n" + encodedMessage + "\n\n");
        try {
            return message.generateACK();
        } catch (IOException e) {
            throw new ReceivingApplicationException(e);
        }
    }

    public boolean canProcess(Message message) {
        return message instanceof ORM_O01;
    }
}