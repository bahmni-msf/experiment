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
        household.villageid = ancRow.villageid;
        household.villagename = ancRow.villagename;

        Patient patient = new Patient();
        patient.id = ancRow.personid;
        patient.membernum = ancRow.membernum;
        patient.membername = ancRow.memName;
        patient.husbandname = ancRow.husbandname;
        patient.householdId = household.id;

        household.addPatient(patient);
        return household;
    }
}