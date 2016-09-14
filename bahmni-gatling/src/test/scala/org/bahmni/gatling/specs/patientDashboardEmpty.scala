package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class patientDashboardEmpty extends Simulation {

    val header = Map(
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")


    val scn = scenario("patientDashboardEmpty")
      .exec(http("request_1")
        .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search?patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
        .headers(header)
        .resources(
            http("request_2")
              .get("/openmrs/ws/rest/v1/patient/757def7a-cdcc-453d-8f9c-34c2b853c11a?v=full")
              .headers(header),
            http("request_3")
              .get("/openmrs/ws/rest/v1/relationship?person=757def7a-cdcc-453d-8f9c-34c2b853c11a&v=full")
              .headers(header),
            http("request_4")
              .get("/openmrs/ws/rest/v1/bahmnicore/observations?concept=Vitals&numberOfVisits=2&patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
              .headers(header),
            http("request_5")
              .get("/openmrs/ws/rest/v1/bahmnicore/orders?concept=Chest&concept=Neck&includeObs=true&numberOfVisits=1&orderTypeUuid=244b43be-28f1-11e4-86a0-005056822b0b&patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
              .headers(header),
            http("request_6")
              .get("/openmrs/ws/rest/v1/visit?includeInactive=true&patient=757def7a-cdcc-453d-8f9c-34c2b853c11a&v=custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
              .headers(header),
            http("request_7")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
              .headers(header),
            http("request_8")
              .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/prescribedAndActive?getEffectiveOrdersOnly=false&getOtherActive=true&numberOfVisits=1&patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
              .headers(header),
            http("request_9")
              .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1&patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
              .headers(header),
            http("request_10")
              .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patient?numberOfVisits=2&patientUuid=757def7a-cdcc-453d-8f9c-34c2b853c11a")
              .headers(header)
        ))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))
}