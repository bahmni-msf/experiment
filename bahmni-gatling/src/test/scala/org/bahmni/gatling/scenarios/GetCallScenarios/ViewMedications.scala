package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

object ViewMedications {

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
      //  getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.admittedPatients")
        /*getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.mypatients_nodisposition"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.todaysPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")*/
      )
  )


  def goToDashboard(patientUuid: String, visitUuid: String): ChainBuilder = {
    exec(
      getRelationship(PATIENT_UUID)
        .resources(
          getPatient(PATIENT_UUID),
          getDiagnoses(PATIENT_UUID),
          getDrugOrderConfig,
          getDrugOrdersForPatient(PATIENT_UUID),
          getOrdersForPatient(PATIENT_UUID, RADIOLOGY_ORDER_TYPE_UUID),
          getOrdersForPatient(PATIENT_UUID, USG_ORDER_TYPE_UUID,
            List("USG Order fulfillment, Clinician", "USG Order fulfillment Notes, Findings", "USG Order fulfillment, Remarks")),
          getProgramEnrollment(PATIENT_UUID),
          getVisits(PATIENT_UUID),
          getObsForDisplayControl(PATIENT_UUID),
          getConcept("All Observation Templates"),
          getLabOrderResults(PATIENT_UUID),
          getVitals(PATIENT_UUID),
          getDisposition(PATIENT_UUID),
          getVisit(visitUuid),
          getGlobalProperty("drugOrder.drugOther"),
          getProgramAttributeTypes,
          getEncounter(PATIENT_UUID, LOGIN_LOCATION_UUID, PROVIDER_UUID)
        )
    )
  }


  def goToNewProgramEnrollment(patientUuid: String): ChainBuilder = {
    exec( getRelationship(PATIENT_UUID)
      .resources(
       addNewProgram(PATIENT_UUID),
       getProgramEnrollment(PATIENT_UUID),
       getProgramAttributeTypes
    )
    )
  }


  def goToViewMedications(patientUuid: String, visitUuid: String): ChainBuilder = {
    exec(
      getRelationship(PATIENT_UUID)
        .resources(
          getProgramEnrollment(PATIENT_UUID),
          launchTreatmentEnrolled,
         //goToDashboard(PATIENT_UUID, VISIT_UUID),
          goToConsultation,
          viewMedications(PATIENT_UUID)
          //  fillMedications(PATIENT_UUID)
        )
    )
  }



  val scn: ScenarioBuilder = scenario("View medications")
    .during(Configuration.Load.DURATION) {
        exec(goToClinicalSearch)
         .pause(50)
        .exec(goToViewMedications(PATIENT_UUID, VISIT_UUID))
        .pause(100)
         .exec(logout)

    }
}
