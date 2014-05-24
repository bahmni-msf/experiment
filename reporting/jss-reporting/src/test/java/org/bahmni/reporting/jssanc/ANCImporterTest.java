package org.bahmni.reporting.jssanc;

import org.bahmni.reporting.repository.AllHouseholds;
import org.junit.Test;

import java.io.IOException;

public class ANCImporterTest {
    @Test
    public void doImport() throws Exception {
        ANCImporter ancImporter = new ANCImporter(new AllHouseholds());
        ancImporter.doImport();
    }
}