package org.bahmni.reporting.repository;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.domain.Patient;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

//
// Fire this command to delete all data from Elastic Search!
// curl  -XDELETE 'http://192.168.33.20:9200/_all'
//
public class AllHouseholds {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final JestClient client;
    private static final String INDEX_NAME = "jss-reporting";
    private static final String HOUSEHOLD_DOC_TYPE = "household";
    private static final String PATIENT_DOC_TYPE = "patient";

    public AllHouseholds() throws Exception {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://192.168.33.20:9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
        client.execute(new CreateIndex.Builder(INDEX_NAME).build());

        defineMappings();
    }

    private void defineMappings() throws Exception {
        defineMapping(PATIENT_DOC_TYPE, "{\n" +
                "  \"" + PATIENT_DOC_TYPE + "\":{\n" +
                "    \"_parent\":{\n" +
                "      \"type\" : \"" + HOUSEHOLD_DOC_TYPE + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

    private void defineMapping(String docType, String mapping) throws Exception {
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                docType,
                mapping
        ).build();
        client.execute(putMapping);
    }

    public void addOrUpdate(Household household) throws Exception {
        String householdJson = objectMapper.writeValueAsString(household);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("id", household.id));
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX_NAME)
                .addType(HOUSEHOLD_DOC_TYPE)
                .build();
        SearchResult result = client.execute(search);

        String householdUUID = null;
        Household exitingHousehold = result.getSourceAsObject(Household.class);
        if (exitingHousehold == null) {
            householdUUID = createHouseholdInES(householdJson);
        } else if (!household.id.trim().equals("999")) {
            System.out.printf("Household %householdJson%n already present", exitingHousehold.id);
        }

        addOrUpdate(household.getFirstPatient(), householdUUID);
    }

    public void addOrUpdate(Patient patient, String householdUUID) throws Exception {
        String patientJson = objectMapper.writeValueAsString(patient);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("id", patient.id));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX_NAME)
                .addType(PATIENT_DOC_TYPE)
                .build();
        SearchResult result = client.execute(search);
        Patient existingPatient = result.getSourceAsObject(Patient.class);

        if (existingPatient != null) {
            System.out.printf(String.format("Patient %s already present", existingPatient.id));
        } else {
            createPatientInES(patientJson, householdUUID);
        }
    }

    private void createPatientInES(String patientJson, String householdUUID) throws Exception {
        Index.Builder builder = new Index.Builder(patientJson).index(INDEX_NAME).type(PATIENT_DOC_TYPE);
        Index index;
        if (householdUUID != null) {
            index = builder.setParameter("parent", householdUUID).build();
        } else {
            index = builder.build();
        }
        JestResult jestResult = client.execute(index);
        System.out.printf(String.format("Patient %s creation status = %s", patientJson, jestResult.isSucceeded()));
    }

    private String createHouseholdInES(String householdJson) throws Exception {
        Index index = new Index.Builder(householdJson).index(INDEX_NAME).type(HOUSEHOLD_DOC_TYPE).build();
        JestResult jestResult = client.execute(index);
        Object id = jestResult.getValue("_id");
        System.out.printf("Household creation status = %s, with id %s", jestResult.isSucceeded(), id);
        return id.toString();
    }
}