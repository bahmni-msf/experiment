package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

object BedManagementFlow {

  private def goToBedManagementQueueSearch: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getProviderForUser(LOGIN_USER_UUID),
        getRegistrationConcepts,
        getPatientConfigFromServer,
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getGlobalProperty("bahmni.encounterType.default"),
        getIdentifierTypes,
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsAdmittoWard"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.admitttokahramana"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsAdmittoRc"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.PatientsMovementtoWard"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.PatientsMovementtoKahramana"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.PatientsMovementtoRc"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsAdmitted"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.PatientsTransferHome"),

      )
  )

  private def getBedsForPatient: ChainBuilder = exec(
    getPatient(BED_MANAGEMENT_PATIENT_UUID)
      .resources(
        getBeds(BED_MANAGEMENT_PATIENT_UUID),
        getConcept("IPD Expected DD"),
        getConcept("Disposition"),
        getAdmissionLocationDetails(ADMISSION_LOCATION_WARD_UUID),
        getConcept("Adt Notes"),
        getBedDetails(BED_ID),
        updateEncounter(BED_MANAGEMENT_PATIENT_UUID,PROVIDER_UUID,LOGIN_LOCATION_UUID),
        assignBedToPatient(BED_ID),
        getPatient(BED_MANAGEMENT_PATIENT_UUID),
        getBeds(BED_MANAGEMENT_PATIENT_UUID),
        getConcept("IPD Expected DD"),
        getAdmissionLocations,
        getVisits(BED_MANAGEMENT_PATIENT_UUID),
        getConcept("IPD Expected DD"),
        getAdmissionLocationDetails(ADMISSION_LOCATION_WARD_UUID),
        getVisitObservations(BED_MANAGEMENT_PATIENT_UUID,"IPD Expected DD")
      )
  )

  val scn: ScenarioBuilder = scenario("Admit a Patient to a Bed")
    .during(Configuration.Load.DURATION) {
      exec(goToBedManagementQueueSearch)
        .exec(getBedsForPatient)
    }
}
