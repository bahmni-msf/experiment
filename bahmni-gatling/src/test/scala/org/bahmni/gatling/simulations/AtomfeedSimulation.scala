package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.{Configuration, HttpRequests}

class AtomfeedSimulation extends Simulation {

  val labFeed = scenario("lab recent scenario")
    .repeat(20) {
      exec(HttpRequests.getLabFeedRecentPage)
        .pause(15)
    }

  val patientFeed = scenario("patient recent scenario")
    .repeat(20) {
      exec(HttpRequests.getPatienFeedRecentPage)
        .pause(15)
    }

  val encounterFeed = scenario("encounter recent scenario")
    .repeat(20) {
      exec(HttpRequests.getEncounterFeedRecentPage)
        .pause(15)
    }

  setUp(encounterFeed.inject(Load.USER_PROFILE),
    patientFeed.inject(Load.USER_PROFILE),
    labFeed.inject(Load.USER_PROFILE))
    .protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.is(100))

}
