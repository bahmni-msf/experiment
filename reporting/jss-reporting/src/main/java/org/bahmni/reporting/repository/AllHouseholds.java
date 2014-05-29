package org.bahmni.reporting.repository;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.bahmni.reporting.domain.Household;
import org.bahmni.reporting.domain.Patient;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.InputStream;
import java.util.Scanner;

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
        String mapping = fileContents("mappings.txt");

        defineMapping(HOUSEHOLD_DOC_TYPE, mapping);
//        defineMapping(PATIENT_DOC_TYPE, "{\n" +
//                "  \"" + PATIENT_DOC_TYPE + "\":{\n" +
//                "    \"_parent\":{\n" +
//                "      \"type\" : \"" + HOUSEHOLD_DOC_TYPE + "\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "}");
    }

    private String fileContents(String fileName) {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(fileName);
        return new Scanner(in, "UTF-8").useDelimiter("\\A").next();
    }

    private void defineMapping(String docType, String mapping) throws Exception {
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                docType,
                mapping
        ).build();
        JestResult jestResult = client.execute(putMapping);
        if (!jestResult.isSucceeded()) {
            throw new RuntimeException(jestResult.getErrorMessage());
        }
    }

    public void addOrUpdate(Household household) throws Exception {
        if (household.id.trim().equals("999")) {
            SearchResult result = findByPatientId(household.getFirstPatient().id);
            if (result.getHits(Household.class).size() > 0) {
                System.out.println(String.format("No household. Patient %s already present", household.getFirstPatient().id));
            } else {
                createHouseholdInES(household);
                System.out.println(String.format("No household. Patient %s created", household.getFirstPatient().id));
            }
        } else {
            SearchResult result = executeQuery(QueryBuilders.matchQuery("id", household.id));
            if (result.getHits(Household.class).size() > 0) {
                Household existingHousehold = result.getFirstHit(Household.class).source;
//                String documentId = result.getValue("_id").toString();
                String documentId = result.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("_id").getAsString();
                if (existingHousehold.hasPatientWithId(household.getFirstPatient().id)) {
                    System.out.println(String.format("Household %s and patient %s exists.", household.id, household.getFirstPatient().id));
                } else {
                    existingHousehold.addPatient(household.getFirstPatient());
                    updateHouseholdInES(existingHousehold, documentId);
                    System.out.println(String.format("Added patient %s to existing household %s", household.getFirstPatient().id, household.id));
                }
            } else {
                createHouseholdInES(household);
                System.out.println(String.format("New household %s and Patient %s", household.id, household.getFirstPatient().id));
            }
        }

//        String householdUUID;
//        Household exitingHousehold = result.getSourceAsObject(Household.class);
//        if (exitingHousehold == null) {
//            householdUUID = createHouseholdInES(household);
//        }
//        else {
//            householdUUID = result.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("_id").getAsString();
//        }
//        addOrUpdate(household.getFirstPatient(), householdUUID);
    }

    public SearchResult findByPatientId(String patientId) throws Exception {
        return executeQuery(QueryBuilders.matchQuery("patients.id", patientId));
    }

    private SearchResult executeQuery(MatchQueryBuilder query) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX_NAME)
                .addType(HOUSEHOLD_DOC_TYPE)
                .build();
        return client.execute(search);
    }

    public void addOrUpdate(Patient patient, String householdUUID) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("id", patient.id));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX_NAME)
                .addType(PATIENT_DOC_TYPE)
                .build();
        SearchResult result = client.execute(search);
        Patient existingPatient = result.getSourceAsObject(Patient.class);

        if (existingPatient != null) {
            System.out.println(String.format("Patient %s already present", existingPatient.id));
        } else {
            createPatientInES(patient, householdUUID);
        }
    }

    private void createPatientInES(Patient patient, String householdUUID) throws Exception {
        String patientJson = objectMapper.writeValueAsString(patient);
        Index.Builder builder = new Index.Builder(patientJson).index(INDEX_NAME).type(PATIENT_DOC_TYPE);
        Index index;
        if (householdUUID != null) {
            index = builder.setParameter("parent", householdUUID).build();
        } else {
            index = builder.build();
        }
        JestResult jestResult = client.execute(index);
        System.out.println(String.format("Household %s, Patient %s, Status = %s", patient.householdId, patient.id, jestResult.isSucceeded()));
    }

    private String createHouseholdInES(Household household) throws Exception {
        String householdJson = objectMapper.writeValueAsString(household);
        Index index = new Index.Builder(householdJson).index(INDEX_NAME).type(HOUSEHOLD_DOC_TYPE).build();
        JestResult jestResult = client.execute(index);
        String id = jestResult.getValue("_id").toString();
        System.out.println(String.format("Household = %s, Status %s", household.id, jestResult.isSucceeded()));

        readMyWrite(id);
        return id;
    }

    private void readMyWrite(String id) throws Exception {
        Get get = new Get.Builder(INDEX_NAME, id).type(HOUSEHOLD_DOC_TYPE).setParameter("realtime", true).build();
        JestResult result = client.execute(get);
        if (!result.isSucceeded()) {
            throw new RuntimeException(result.getErrorMessage());
        }
    }

    private void updateHouseholdInES(Household household, String documentId) throws Exception {
        String householdJson = objectMapper.writeValueAsString(household);
        Update updateRequest = new Update.Builder(householdJson).index(INDEX_NAME).type(HOUSEHOLD_DOC_TYPE).id(documentId).build();
        JestResult jestResult = client.execute(updateRequest);
        String id = jestResult.getValue("_id").toString();
        System.out.println(String.format("Household = %s, Status %s", household.id, jestResult.isSucceeded()));
        readMyWrite(id);
    }
//
//    public Household findByPatientId(String patientId) throws Exception {
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(fileContents("searchByPatient"));
//        Search search = new Search.Builder(searchSourceBuilder.toString())
//                .addIndex(INDEX_NAME)
//                .addType(HOUSEHOLD_DOC_TYPE)
//                .build();
//        SearchResult searchResult = client.execute(search);
//        return searchResult.getFirstHit(Household.class).source;
//    }
}