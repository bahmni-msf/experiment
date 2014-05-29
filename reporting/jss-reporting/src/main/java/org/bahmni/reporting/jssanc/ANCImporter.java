package org.bahmni.reporting.jssanc;

import org.bahmni.csv.CSVFile;
import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.repository.AllHouseholds;

public class ANCImporter {
    private AllHouseholds allHouseholds;

    public ANCImporter(AllHouseholds allHouseholds) {
        this.allHouseholds = allHouseholds;
    }

    public void doImport() throws Exception {
        CSVFile<ANCRow> csvFile = new CSVFile<ANCRow>(".", "ANC.csv", ANCRow.class);
        csvFile.openForRead();

        ANCRowMapper ancRowMapper = new ANCRowMapper();
        ANCRow csvRow;
        while((csvRow = csvFile.readEntity()) != null) {
            Household household = ancRowMapper.map(csvRow);
            allHouseholds.addOrUpdate(household);
        }
    }
}