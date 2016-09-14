package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class patientDashboardWithAllTemplates extends Simulation {

    val header = Map(
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")

    //    15 lab orders, 10 Diagnosis, 20 obs templates(586 obs)

    val scn = scenario("patientDashboadWithAllObservations")
      .exec(http("request_1")
        .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search?patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
        .headers(header)
        .resources(
            http("request_2")
              .get("/openmrs/ws/rest/v1/patient/7c58fb34-ed99-4488-aff1-c644e9673b90?v=full")
              .headers(header),
            http("request_3")
              .get("/openmrs/ws/rest/v1/relationship?person=7c58fb34-ed99-4488-aff1-c644e9673b90&v=full")
              .headers(header),
            http("request_4")
              .get("/openmrs/ws/rest/v1/bahmnicore/observations?concept=Vitals&numberOfVisits=2&patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
              .headers(header),
            http("request_5")
              .get("/openmrs/ws/rest/v1/bahmnicore/orders?concept=Chest&concept=Neck&includeObs=true&numberOfVisits=1&orderTypeUuid=244b43be-28f1-11e4-86a0-005056822b0b&patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
              .headers(header),
            http("request_6")
              .get("/openmrs/ws/rest/v1/visit?includeInactive=true&patient=7c58fb34-ed99-4488-aff1-c644e9673b90&v=custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
              .headers(header),
            http("request_7")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
              .headers(header),
            http("request_8")
              .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/prescribedAndActive?getEffectiveOrdersOnly=false&getOtherActive=true&numberOfVisits=1&patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
              .headers(header),
            http("request_9")
              .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1&patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
              .headers(header),
            http("request_10")
              .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patient?numberOfVisits=2&patientUuid=7c58fb34-ed99-4488-aff1-c644e9673b90")
              .headers(header)
        ))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))
}