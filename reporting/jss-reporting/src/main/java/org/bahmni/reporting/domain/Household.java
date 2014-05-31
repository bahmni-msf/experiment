package org.bahmni.reporting.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Household {
    public String cluster;
    public String id;
    public String villagename;
    public String postOffice;
    public String tehsil;
    public List<Patient> patients = new ArrayList<Patient>();
    public String villageid;

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    @JsonIgnore
    public Patient getFirstPatient() {
        return patients.get(0);
    }

    public boolean hasPatientWithId(String id) {
        for (Patient patient : patients) {
            if (patient.id.equals(id)) return true;
        }
        return false;
    }
}