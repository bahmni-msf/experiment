package org.bahmni.gatling.specs

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.HttpRequests._
import org.bahmni.gatling.spec.Configuration
import org.bahmni.gatling.spec.Configuration.Constants._

class RegistrationSearch extends Simulation {

  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )

  var goToHomePage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getLoginLocations
      )
  )

  val goToRegistrationSearchPage: ChainBuilder = exec(
    getVisitLocation(LOGIN_LOCATION_UUID)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getAddressHierarchyLevel,
        getIdentifierTypes,
        getRelationshipTypes,
        getEntityMapping,
        getPersonAttributeTypes,
        getRegistrationConcepts
      )

  )

  val performSearch: ChainBuilder = exec(
    searchPatientUsingIdentifier(LOGIN_LOCATION_UUID, PATIENT_IDENTIFIER)
  )

  val scn: ScenarioBuilder = scenario("registerPatient")
    .exec(login)
    .exec(goToHomePage)
    .exec(goToRegistrationSearchPage)
    .exec(performSearch)

  setUp(scn.inject(Configuration.Load.USER_PROFILE))
    .protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.is(100))

}