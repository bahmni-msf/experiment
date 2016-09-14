package org.bahmni.gatling.spec
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class clinicalSearchAll extends Simulation {

    val header = Map(
        "Accept" -> "application/json, text/plain, */*",
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")

    val scn = scenario("clinicalSearchAll")
      .exec(http("request_0")
        .get("https://benchmark.mybahmni.org/openmrs/ws/rest/v1/bahmnicore/search/patient?loginLocationUuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&q=performance&startIndex=0")
        .headers(header))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))

}