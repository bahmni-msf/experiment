package org.bahmni.module;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExampleApplication implements Application {
    @Override
    public Message processMessage(Message message) throws ApplicationException, HL7Exception {
        String encodedMessage = new PipeParser().encode(message);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");
// Now generate a simple acknowledgment message and return it
        return message;
    }

    public boolean canProcess(Message theIn) {
        return true;
    }
}