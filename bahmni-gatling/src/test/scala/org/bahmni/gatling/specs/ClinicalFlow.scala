package org.bahmni.gatling.specs

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.HttpRequests._
import org.bahmni.gatling.spec.Configuration
import org.bahmni.gatling.spec.Configuration.Constants._

class ClinicalFlow extends Simulation {

  val goToClinicalApp: ChainBuilder = exec(
    getPatientConfigFromServer
      .resources(
        getConcept("Consultation Note"),
        getConcept("Lab Order Notes"),
        getConcept("Impression"),
        getConcept("Dosage Frequency"),
        getConcept("Dosage Instructions"),
        getConcept("All_Tests_and_Panels")
          .queryParam("v", "custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.encounterType.default"),
        getRegistrationConcepts,
        getIdentifierTypes,
        getOrderTypes
      )
  )

  val goToClinicalSearch: ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .resources(
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.admittedPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.mypatients_nodisposition"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      )
  )


  private def gotToDashboard(patientUuid: String, visitUuid: String): ChainBuilder = {
    exec(
      getRelationship(patientUuid)
        .resources(
          getPatient(patientUuid),
          getDiagnoses(patientUuid),
          getDrugOrderConfig,
          getDrugOrdersForPatient(patientUuid),
          getOrdersForPatient(patientUuid, RADIOLOGY_ORDER_TYPE_UUID),
          getOrdersForPatient(patientUuid, USG_ORDER_TYPE_UUID,
            List("USG Order fulfillment, Clinician", "USG Order fulfillment Notes, Findings", "USG Order fulfillment, Remarks")),
          getProgramEnrollment(patientUuid),
          getVisits(patientUuid),
          getObsForDisplayControl(patientUuid),
          getConcept("All Observation Templates"),
          getLabOrderResults(patientUuid),
          getVitals(patientUuid),
          getDisposition(patientUuid),
          getVisit(visitUuid),
          getGlobalProperty("drugOrder.drugOther"),
          getProgramAttributeTypes,
          getEncounter(patientUuid, LOGIN_LOCATION_UUID, PROVIDER_UUID)
        )
    )
  }

  val scn: ScenarioBuilder = scenario("clinical search")
    .exec(goToClinicalApp)
    .exec(goToClinicalSearch)
    .exec(gotToDashboard(PATIENT_UUID, VISIT_UUID))
    .pause(30)
    .exec(goToClinicalSearch)
    .exec(gotToDashboard(PATIENT_UUID, VISIT_UUID))


  setUp(scn.inject(Configuration.Load.USER_PROFILE))
    .protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.is(100))

}
