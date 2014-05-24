package org.bahmni.reporting.jssanc;

import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.domain.Patient;

public class ANCRowMapper {
    public Household map(ANCRow ancRow) {
        Household household = new Household();
        household.id = ancRow.familynum;
        household.cluster = ancRow.cluster;

        Patient patient = new Patient();
        patient.id = ancRow.personid;
        patient.householdId = household.id;

        household.addPatient(patient);
        return household;
    }
}