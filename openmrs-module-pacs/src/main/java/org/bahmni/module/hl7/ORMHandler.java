package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

import java.io.IOException;
import java.util.Map;

public class ORMHandler implements ReceivingApplication {
    @Override
    public Message processMessage(Message message, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
        ORM_O01 ormMessage = (ORM_O01) message;
        String messageControlId = ormMessage.getMSH().getMessageControlID().getValue();
        String sendingFacility = ormMessage.getMSH().getSendingFacility().getName();
        System.out.println("messagecontrolid:'" + messageControlId + "'");
        System.out.println("facility:'" + sendingFacility + "'");

        String encodedMessage = new PipeParser().encode(message);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");
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