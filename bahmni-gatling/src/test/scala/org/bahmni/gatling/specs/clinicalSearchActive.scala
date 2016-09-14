package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class clinicalSearchActive extends Simulation {

    val header = Map(
        "Accept" -> "application/json, text/plain, */*",
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")

    val scn = scenario("clinicalSearchActive")
      .exec(http("request_0")
        .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.activePatients&v=full")
        .headers(header))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))

}