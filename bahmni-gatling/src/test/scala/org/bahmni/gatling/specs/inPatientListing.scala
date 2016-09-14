package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class inPatientListing extends Simulation {

    val headers_1 = Map(
        "Cache-Control" -> "no-cache",
        "Pragma" -> "no-cache")


    val scn = scenario("inPatientListing")
      .exec(http("request_1")
        .get("/openmrs/ws/rest/v1/user?username=superman&v=custom:(username,uuid,person:(uuid,),privileges:(name,retired),userProperties)")
        .headers(headers_1)
        .resources(
            http("request_2")
              .get("/openmrs/ws/rest/v1/provider?user=12a02226-5bb1-4ad0-bcca-14e74e1f755b")
              .headers(headers_1),
            http("request_3")
              .get("/openmrs/ws/rest/v1/provider?user=12a02226-5bb1-4ad0-bcca-14e74e1f755b")
              .headers(headers_1),
            http("request_4")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
              .headers(headers_1),
            http("request_5")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/patient")
              .headers(headers_1),
            http("request_6")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty?property=mrs.genders")
              .headers(headers_1),
            http("request_7")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty?property=bahmni.relationshipTypeMap")
              .headers(headers_1),
            http("request_8")
              .get("/openmrs/ws/rest/v1/admissionLocation/")
              .headers(headers_1),
            http("request_9")
              .get("/openmrs/ws/rest/v1/idgen/identifiertype")
              .headers(headers_1),
            http("request_10")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.patientsToAdmit&v=full")
              .headers(headers_1),
            http("request_11")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.patientsToAdmit&v=full")
              .headers(headers_1),
            http("request_12")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.admittedPatients&v=full")
              .headers(headers_1),
            http("request_13")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.patientsToDischarge&v=full")
              .headers(headers_1),
            http("request_14")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_name=Location-23&q=emrapi.sqlGet.wardsListDetails&v=full")
              .headers(headers_1)))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))

}