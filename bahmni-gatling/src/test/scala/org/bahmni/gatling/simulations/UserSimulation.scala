package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration.HttpConf._
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.scenarios.{AdtFlow, AtomfeedScenarios, ClinicalFlow, RegistrationSearch}

class UserSimulation extends Simulation {

  setUp(
    RegistrationSearch.scn.inject(rampUsers(3) over 10).protocols(HTTPS_PROTOCOL),
    ClinicalFlow.scn.inject(rampUsers(20) over 20).protocols(HTTPS_PROTOCOL),
    AdtFlow.scn.inject(rampUsers(2) over 10).protocols(HTTPS_PROTOCOL),
    AtomfeedScenarios.patientFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.patientFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL)
  )
    .assertions(global.successfulRequests.percent.gte(90))
}
