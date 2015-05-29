package org.bahmni.module.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

import java.io.IOException;
import java.util.Map;

public class ORUHandler implements ReceivingApplication {

    @Override
    public Message processMessage(Message message, Map<String, Object> stringObjectMap) throws ReceivingApplicationException, HL7Exception {
        ORU_R01 oruR01 = (ORU_R01) message;
        String messageControlID = oruR01.getMSH().getMessageControlID().getValue();
        String sendingFacility = oruR01.getMSH().getSendingFacility().getName();;
        String patientIdentifier = oruR01.getRESPONSE().getPATIENT().getPID().getPatientIDInternalID(0).getID().getValue();

        System.out.println("messagecontrolid:'" + messageControlID + "'");
        System.out.println("facility:'" + sendingFacility + "'");
        System.out.println("Patient Id:'" + patientIdentifier + "'");

        String encodedMessage = new PipeParser().encode(message);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");

        try {
            return message.generateACK();
        } catch (IOException e) {
            throw new ReceivingApplicationException(e);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        return message instanceof ORU_R01;
    }
}
