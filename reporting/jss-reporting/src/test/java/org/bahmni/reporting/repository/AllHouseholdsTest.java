package org.bahmni.reporting.repository;

import io.searchbox.core.SearchResult;
import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.domain.Patient;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AllHouseholdsTest {
    @Test
    @Ignore
    public void addOrUpdate() throws Exception {
        AllHouseholds allHouseholds = new AllHouseholds();

        Household household = createHousehold("11");
        allHouseholds.addOrUpdate(household);

        household = createHousehold("12");
        allHouseholds.addOrUpdate(household);
    }

    @Test
    public void findByPatient() throws Exception {
        AllHouseholds allHouseholds = new AllHouseholds();
        Household household = createHousehold("12");
        allHouseholds.addOrUpdate(household);
        SearchResult searchResult = allHouseholds.findByPatientId("12");
        assertEquals(1, searchResult.getHits(Household.class).size());
    }

    private Household createHousehold(String patientId) {
        Household household = new Household();
        household.id = "1";
        household.tehsil = "t1";
        createPatient(household, patientId);
        return household;
    }

    private void createPatient(Household household, String patientId) {
        Patient patient = new Patient();
        patient.id = patientId;
        patient.householdId = "1";
        household.addPatient(patient);
    }
}