package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.scenarios.{AdtFlow, ClinicalFlow, RegistrationSearch}

class UserSimulation extends Simulation {

  setUp(RegistrationSearch.scn.inject(Load.USER_PROFILE),
    ClinicalFlow.scn.inject(Load.USER_PROFILE),
    AdtFlow.scn.inject(Load.USER_PROFILE))
    .protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.is(100))
}
