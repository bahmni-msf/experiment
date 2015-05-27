package org.bahmni.module;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.parser.PipeParser;

public class ORMHandler implements Application {
    @Override
    public Message processMessage(Message message) throws ApplicationException, HL7Exception {
        ORM_O01 ormMessage = (ORM_O01) message;
        String messageControlId = ormMessage.getMSH().getMessageControlID().getValue();
        String sendingFacility = ormMessage.getMSH().getSendingFacility().getName();
        System.out.println("messagecontrolid:'" + messageControlId + "'");
        System.out.println("facility:'" + sendingFacility + "'");

        String encodedMessage = new PipeParser().encode(message);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");

        return HL7Utils.generateACK(messageControlId, sendingFacility);
    }

    public boolean canProcess(Message message) {
        return message instanceof ORM_O01;
    }
}