package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class patientDashboard extends Simulation {


    val header = Map(
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")

    // Existing patient with 7 tempates(1500) observations, 6 diagnosis, 1 lab order

    val scn = scenario("patientDashboadWith1500Observations")
      .exec(http("request_1")
        .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search?patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
        .headers(header)
        .resources(
            http("request_2")
              .get("/openmrs/ws/rest/v1/patient/abe60b9e-3792-11e6-b60d-d4ae52d4c47d?v=full")
              .headers(header),
            http("request_3")
              .get("/openmrs/ws/rest/v1/relationship?person=abe60b9e-3792-11e6-b60d-d4ae52d4c47d&v=full")
              .headers(header),
            http("request_4")
              .get("/openmrs/ws/rest/v1/bahmnicore/observations?concept=Vitals&numberOfVisits=2&patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
              .headers(header),
            http("request_5")
              .get("/openmrs/ws/rest/v1/bahmnicore/orders?concept=Chest-AP&concept=Chest-PA&includeObs=true&numberOfVisits=1&orderTypeUuid=244b43be-28f1-11e4-86a0-005056822b0b&patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
              .headers(header),
            http("request_6")
              .get("/openmrs/ws/rest/v1/visit?includeInactive=true&patient=abe60b9e-3792-11e6-b60d-d4ae52d4c47d&v=custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
              .headers(header),
            http("request_7")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
              .headers(header),
            http("request_8")
              .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/prescribedAndActive?getEffectiveOrdersOnly=false&getOtherActive=true&numberOfVisits=1&patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
              .headers(header),
            http("request_9")
              .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1&patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
              .headers(header),
            http("request_10")
              .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patient?numberOfVisits=2&patientUuid=abe60b9e-3792-11e6-b60d-d4ae52d4c47d")
              .headers(header)
        ))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
//    setUp(scn.inject(rampUsers(10) over(5 seconds))).protocols(httpProtocol)
//      .assertions(global.successfulRequests.percent.is(100))

}