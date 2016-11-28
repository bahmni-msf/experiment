package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import org.bahmni.gatling.HttpRequests._
import org.bahmni.gatling.spec.Configuration.Constants._

class inPatientListing extends Simulation {

  val goToADTApp = {
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

  val scn = scenario("inPatientListing")
    .exec(goToADTApp)

  setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.is(100))

}