package org.bahmni.reporting.jssanc;

import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.domain.Patient;

public class ANCRowMapper {
    public Household map(ANCRow ancRow) {
        Household household = new Household();
        household.id = ancRow.familynum;
        household.cluster = ancRow.cluster;
        household.postOffice = ancRow.postoffice;
        household.tehsil = ancRow.tehsilid;
        household.villagename = ancRow.villagename;

        Patient patient = new Patient();
        patient.id = ancRow.personid;
        patient.householdId = household.id;
        patient.cluster = ancRow.cluster;
        patient.postOffice = ancRow.postoffice;
        patient.tehsil = ancRow.tehsilid;
        patient.villagename = ancRow.villagename;

        household.addPatient(patient);
        return household;
    }
}