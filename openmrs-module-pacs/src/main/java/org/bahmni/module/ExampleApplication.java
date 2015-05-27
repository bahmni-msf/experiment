package org.bahmni.module;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ACK;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.IOException;

public class ExampleApplication implements Application {
    @Override
    public Message processMessage(Message message) throws ApplicationException, HL7Exception {
        String encodedMessage = new PipeParser().encode(message);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");
        Message returnACK = new ACK();
        try {
            returnACK = message.generateACK();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                returnACK = message.generateACK(AcknowledgmentCode.CR, new HL7Exception("Exception Custom"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return returnACK;
    }

    public boolean canProcess(Message theIn) {
        return true;
    }
}