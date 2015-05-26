package org.bahmni.module;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.SimpleServer;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HL7InterFacePacs {
    private HL7Service hl7Service;


    public HL7InterFacePacs() {

    }

    public void initialize() {

        hl7Service = new SimpleServer(8842,
                new MinLowerLayerProtocol(), new PipeParser());
        Map<String, Application> handlers = new HashMap<String, Application>();
        handlers.put("ORM^001", new DefaultApplication());
        for (Map.Entry<String,Application>  entry : handlers.entrySet()) {
            String messageType = entry.getKey().split("_")[0];
            String triggerEvent = entry.getKey().split("_").length > 1 ? entry.getKey().split("_") [1] : null;
            hl7Service.registerApplication(messageType, triggerEvent, entry.getValue());
        }

        hl7Service.start();

    }

    public String createORMDicomHL7Message() throws HL7Exception {
        Parser parser = new PipeParser();
        ORM_O01 message = new ORM_O01();

        // handle the MSH component
        MSH msh = message.getMSH();
        HL7Utils.populateMessageHeader(msh, new Date(), "ORM", "O01", "Bahmni EMR");

        // handle the patient PID component
        PID pid = message.getPATIENT().getPID();
        pid.getPatientIDInternalID(0).getID().setValue("GAN0'001");
        pid.getPatientName().getFamilyName().setValue("Gond");
        pid.getPatientName().getGivenName().setValue("Ramesh");
        pid.getDateOfBirth().getTimeOfAnEvent().setValue("08-08-2012");
        pid.getSex().setValue("M");
        // TODO: do we need patient admission ID / account number

        PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();

        pv1.getAssignedPatientLocation().getPointOfCare().setValue("Location");

            pv1.getAssignedPatientLocation().getLocationType().setValue("EMR");

        pv1.getReferringDoctor(0).getIDNumber().setValue("ProviderId");
                pv1.getReferringDoctor(0).getFamilyName().setValue("Sharma");
                pv1.getReferringDoctor(0).getGivenName().setValue("Provider");

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getOrderControl().setValue("ordercontrol");

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();
        obr.getFillerOrderNumber().getEntityIdentifier().setValue("ORNO1");
        obr.getUniversalServiceIdentifier().getIdentifier().setValue("XXX");

        obr.getUniversalServiceIdentifier().getText()
                .setValue("XX");

        // note that we are just sending modality here, not the device location
        obr.getPlacerField2().setValue("HH");
        obr.getQuantityTiming().getPriority().setValue("STAT");
        obr.getScheduledDateTime().getTimeOfAnEvent().setValue(HL7Utils.getHl7DateFormat().format("08-07-2015"));

        // break the reason for study up by lines
        obr.getReasonForStudy(0).getText().setValue("line");

        return parser.encode(message);
    }

    //Send HL7 ORU message to dcm4chee.
    public static int sendHL7Worklist(String hl7blob) {
        String input[] = { "-c", "10.0.0.30" + ":2575", hl7blob };
        //String input[]={"--help"};
        int result = HL7Snd.main(input);
        return result;
    }

}
