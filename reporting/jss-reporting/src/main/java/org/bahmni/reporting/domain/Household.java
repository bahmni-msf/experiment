package org.bahmni.reporting.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Household {
    public String cluster;
    public String id;

    @JsonIgnore
    public List<Patient> patients = new ArrayList<Patient>();

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    @JsonIgnore
    public Patient getFirstPatient() {
        return patients.get(0);
    }
}