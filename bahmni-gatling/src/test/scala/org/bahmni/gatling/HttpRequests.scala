package org.bahmni.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.bahmni.gatling.spec.Configuration.Constants._

object HttpRequests {

  def getUser(username: String): HttpRequestBuilder = {
    http("get user")
      .get("/openmrs/ws/rest/v1/user?v=custom:(username,uuid,person:(uuid,),privileges:(name,retired),userProperties)")
      .queryParam("username", username)
  }

  def getGlobalProperty(property: String): HttpRequestBuilder = {
    http("get " + property + " global property")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty")
      .queryParam("property", property)
  }

  def getProviderForUser(userUuid: String): HttpRequestBuilder = {
    http("get provider")
      .get("/openmrs/ws/rest/v1/provider")
      .queryParam("user", userUuid)
  }

  def getLoginLocations: HttpRequestBuilder = {
    http("get login locations")
      .get("/openmrs/ws/rest/v1/location?operator=ALL&s=byTags&tags=Login+Location&v=default")
  }

  def getAddressHierarchyLevel: HttpRequestBuilder = {
    http("get address hierarchy level")
      .get("/openmrs/module/addresshierarchy/ajax/getOrderedAddressHierarchyLevels.form")
  }

  def getVisitLocation(loginLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/bahmnicore/visitLocation/" + loginLocationUuid)
  }

  def getIdentifierTypes: HttpRequestBuilder = {
    http("get identifier types")
      .get("/openmrs/ws/rest/v1/idgen/identifiertype")
  }

  def getRelationshipTypes: HttpRequestBuilder = {
    http("get relationship types")
      .get("/openmrs/ws/rest/v1/relationshiptype?v=custom:(aIsToB,bIsToA,uuid)")
  }

  def getOrderTypes: HttpRequestBuilder = {
    http("get order types")
      .get("/openmrs/ws/rest/v1/ordertype?v=custom:(uuid,display,conceptClasses:(uuid,display,name))")
  }

  def getEntityMapping: HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=loginlocation_visittype&s=byEntityAndMappingType")
  }

  def getPersonAttributeTypes: HttpRequestBuilder = {
    http("get person attribute types")
      .get("/openmrs/ws/rest/v1/personattributetype?v=custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def getRegistrationConcepts: HttpRequestBuilder = {
    http("get registration concepts")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
  }

  def searchPatientUsingIdentifier(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("search patient " + identifier)
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("loginLocationUuid", loginLocationUuid)
      .queryParam("identifier", identifier)
      .queryParam("addressFieldName", "city_village")
      .queryParam("addressSearchResultsConfig", "city_village")
      .queryParam("addressSearchResultsConfig", "address1")
      .queryParam("filterOnAllIdentifiers", "true")
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }

  def getPatientConfigFromServer: HttpRequestBuilder = {
    http("patient config")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/patient")
  }

  def getConcept(name: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&v=custom:(uuid,name,answers)")
      .queryParam("name", name)
  }

  def getPatientsInSearchTab(locationUuid: String, providerUuid: String, sqlName: String): HttpRequestBuilder = {
    http(sqlName)
      .get("/openmrs/ws/rest/v1/bahmnicore/sql")
      .queryParam("location_uuid", locationUuid)
      .queryParam("provider_uuid", providerUuid)
      .queryParam("q", sqlName)
      .queryParam("v", "full")
  }

  def getRelationship(personUuid: String): HttpRequestBuilder = {
    http("get relationship")
      .get("/openmrs/ws/rest/v1/relationship")
      .queryParam("person", personUuid)
  }

  def getPatient(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patient/" + patientUuid)
  }

  def getDiagnoses(patientUuid: String): HttpRequestBuilder = {
    http("get diagnoses for patient")
      .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search")
      .queryParam("patientUuid", patientUuid)
  }

  def getDrugOrderConfig: HttpRequestBuilder = {
    http("get config for drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
  }

  def getDrugOrdersForPatient(patientUuid: String): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/prescribedAndActive")
      .queryParam("getEffectiveOrdersOnly", "false")
      .queryParam("getOtherActive", "true")
      .queryParam("numberOfVisits", 1)
      .queryParam("patientUuid", patientUuid)
  }

  def getOrdersForPatient(patientUuid: String, orderTypeUuid: String): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/orders")
      .queryParam("includeObs", "true")
      .queryParam("numberOfVisits", 4)
      .queryParam("patientUuid", patientUuid)
      .queryParam("orderTypeUuid", orderTypeUuid)
  }

  def getOrdersForPatient(patientUuid: String, orderTypeUuid: String, concepts: List[String]): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/orders")
      .queryParam("includeObs", "true")
      .queryParam("numberOfVisits", 1)
      .queryParam("patientUuid", patientUuid)
      .queryParam("orderTypeUuid", orderTypeUuid)
      .queryParam("concepts", concepts)
  }

  def getProgramEnrollment(patientUuid: String): HttpRequestBuilder = {
    http("get program enrollment")
      .get("/openmrs/ws/rest/v1/bahmniprogramenrollment?v=full")
      .queryParam("patient", patientUuid)
  }

  def getVisits(patientUuid: String): HttpRequestBuilder = {
    http("getVisits")
      .get("/openmrs/ws/rest/v1/visit?includeInactive=true&v=custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
      .queryParam("patient", patientUuid)
  }

  def getObsForDisplayControl(patientUuid: String): HttpRequestBuilder = {
    http("get obs")
      .get("/openmrs/ws/rest/v1/obs?conceptNames=Patient+Vitals&conceptNames=Acute+OPD+visit" +
        "&conceptNames=Acute+Abdominal+Complaint&conceptNames=Acute+HEENT+Complaint&conceptNames=Acute+MSK+Complaint" +
        "&conceptNames=Acute+Skin+Complaint&conceptNames=Acute+GU+Complaint&conceptNames=ANC" +
        "&conceptNames=Asthma,+Progress&conceptNames=Childhood+Illness+(Children+aged+below+2+months)" +
        "&conceptNames=Childhood+Illness(+Children+aged+2+months+to+5+years)&conceptNames=CHP+Antenatal+Care" +
        "&conceptNames=CHP+Chronic+Disease+Counseling&conceptNames=CHP+Registration" +
        "&conceptNames=Chronic+Kidney+Disease,+Progress&conceptNames=COPD,+Progress" +
        "&conceptNames=Congestive+Heart+Failure,+Progress&conceptNames=Death+Note" +
        "&conceptNames=Delivery+note&conceptNames=Dental&conceptNames=Dental+Note&conceptNames=Depression+Note" +
        "&conceptNames=Diabetes,+Progress&conceptNames=Discharge+Note&conceptNames=DRTuberculosis,+Intake" +
        "&conceptNames=ECG+Notes&conceptNames=ER+General+Notes&conceptNames=Family+Planning+Template" +
        "&conceptNames=HIV+Testing+and+Counseling+Intake+Template&conceptNames=HIV+Treatment+and+Care+Intake+Template" +
        "&conceptNames=HIV+Treatment+and+Care+Progress+Template&conceptNames=Hypertension,+Progress" +
        "&conceptNames=IMAM+Program&conceptNames=Kalaazar,+Template&conceptNames=Leprosy,+Template" +
        "&conceptNames=Mental+Health&conceptNames=Mental+Health+Screener&conceptNames=Malaria" +
        "&conceptNames=Nutrition&conceptNames=Operative+Notes&conceptNames=Opioid+Substitution+Therapy+Intake+Template" +
        "&conceptNames=Opioid+Substitution+Therapy+Intake+Template&conceptNames=Opportunistic+Infection+Template" +
        "&conceptNames=Orthopaedic+Examination&conceptNames=PNC+Note&conceptNames=Post+Delivery+Family+Planning+Checklist" +
        "&conceptNames=Prevention+of+Mother-to-Child+Transmission+Intake+Template&conceptNames=Procedure+Notes" +
        "&conceptNames=Psychiatrist%27s+Recommendation&conceptNames=Psychosis+Followup+Note" +
        "&conceptNames=Psychosis+Intake+Note&conceptNames=Rheumatoid+Arthritis,+Progress" +
        "&conceptNames=Rheumatic+Heart+Disease,+Progress&conceptNames=Safe+Abortion" +
        "&conceptNames=Seizure+Disorder,+Progress&conceptNames=Sextually+Transmitted+Infections+Intake+Template" +
        "&conceptNames=Stroke+OPD&conceptNames=Trauma+Notes&conceptNames=Tuberculosis,+Intake&conceptNames=USG+Notes" +
        "&numberOfVisits=1&s=byPatientUuid&v=visitFormDetails")
      .queryParam("patient", patientUuid)
  }

  def getLabOrderResults(patientUuid: String): HttpRequestBuilder = {
    http("get lab order results")
      .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getVitals(patientUuid: String): HttpRequestBuilder = {
    http("get vitals")
      .get("/openmrs/ws/rest/v1/bahmnicore/observations?numberOfVisits=2")
      .queryParam("concept", "Vitals")
      .queryParam("patientUuid", patientUuid)
  }

  def getDisposition(patientUuid: String): HttpRequestBuilder = {
    http("get dispostion")
      .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patient?numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getVisit(visitUuid: String): HttpRequestBuilder = {
    http("get visit")
      .get("/openmrs/ws/rest/v1/visit/" + visitUuid + "?v=custom:(attributes:(value,attributeType:(display,name)))")
  }

  def getPatientImage(patientUuid: String): HttpRequestBuilder = {
    http("get patient image")
      .get("/openmrs/ws/rest/v1/patientImage")
      .queryParam("patientUuid", patientUuid)
  }

  def getProgramAttributeTypes: HttpRequestBuilder = {
    http("get program attribute types")
      .get("/openmrs/ws/rest/v1/programattributetype?v=custom:(uuid,name,description,datatypeClassname,datatypeConfig,concept)")
  }

  def getEncounter(patientUuid: String, locationUuid: String, providerUuid: String): HttpRequestBuilder = {
    http("find encounter")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/find")
      .body(StringBody(s"""{"patientUuid":"$patientUuid","providerUuids":["$providerUuid"],"includeAll":false,"encounterDateTimeFrom":null,"encounterDateTimeTo":null,"encounterTypeUuids":["$ENCOUNTER_TYPE_UUID"],"locationUuid":"$locationUuid"}"""))
      .asJSON
  }

}
