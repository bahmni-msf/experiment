package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests.{getProviderForUser, _}

object AdtFlow {

  val goToADTApp: HttpRequestBuilder = {
    getUser(LOGIN_USER)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getProviderForUser(LOGIN_USER_UUID),
        getRegistrationConcepts,
        getPatientConfigFromServer,
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getIdentifierTypes,
        getAdmissionLocations,
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsToAdmit"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsToAdmit"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.admittedPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsToDischarge"),
        getWardListDetails("Location-23"),
        getWardListDetails("Location-25")
      )
  }

  val scn: ScenarioBuilder = scenario("inPatientListing")
    .during(Configuration.Load.DURATION) {
      exec(goToADTApp)
    }
}