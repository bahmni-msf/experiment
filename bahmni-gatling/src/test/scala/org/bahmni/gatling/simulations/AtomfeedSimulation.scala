package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.HttpRequests._

class AtomfeedSimulation extends Simulation {

  val headers = Map(
    "Cache-Control" -> "no-cache",
    "Disable-WWW-Authenticate" -> "true",
    "Pragma" -> "no-cache",
    "Connection" -> "keep-alive")

  val labFeed = scenario("lab recent scenario")
    .repeat(20) {
      exec(getLabFeedRecentPage.headers(headers))
        .pause(15)
    }

  val patientFeed = scenario("patient recent scenario")
    .repeat(20) {
      exec(getPatienFeedRecentPage.headers(headers))
        .pause(15)
    }

  val encounterFeed = scenario("encounter recent scenario")
    .repeat(20) {
      exec(getEncounterFeedRecentPage.headers(headers))
        .pause(15)
    }

  val encounterFeedContent = scenario("encounter feed content scenario")
    .repeat(20) {
      exec(getEncounterFeedcontent(ATOMFEED_ENCOUNTER_UUID).headers(headers))
        .pause(15)
    }

  val patientFeedContent = scenario("patient feed content scenario")
    .repeat(20) {
      exec(getPatientFeedcontent(PATIENT_UUID).headers(headers))
        .pause(15)
    }

  val labFeedContent = scenario("lab feed content scenario")
    .repeat(20) {
      exec(getLabFeedcontent(ALL_TESTS_AND_PANELS).headers(headers))
        .pause(15)
    }

  setUp(encounterFeed.inject(Iterable(rampUsers(3) over 10)),
    patientFeed.inject(Load.USER_PROFILE),
    patientFeedContent.inject(Load.USER_PROFILE),
    labFeedContent.inject(Load.USER_PROFILE),
    encounterFeedContent.inject(Load.USER_PROFILE),
    labFeed.inject(Load.USER_PROFILE))
    .protocols(Configuration.HttpConf.HTTP_PROTOCOL)
    .assertions(global.successfulRequests.percent.gte(90))

}
