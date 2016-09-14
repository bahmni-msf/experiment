package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class clinicalSearchListing extends Simulation {

    val headers_1 = Map(
        "Accept" -> "application/json, text/plain, */*",
        "Cache-Control" -> "no-cache",
        "Disable-WWW-Authenticate" -> "true",
        "Pragma" -> "no-cache")

    val scn = scenario("clinicalSearchListing")
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
              .get("/openmrs/ws/rest/v1/bahmnicore/config/patient")
              .headers(headers_1),
            http("request_5")
              .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
              .headers(headers_1),
            http("request_6")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Consultation+Note&v=custom:(uuid,name,answers)")
              .headers(headers_1),
            http("request_7")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Lab+Order+Notes&v=custom:(uuid,name)")
              .headers(headers_1),
            http("request_8")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Impression&v=custom:(uuid,name)")
              .headers(headers_1),
            http("request_9")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=All_Tests_and_Panels&v=custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))")
              .headers(headers_1),
            http("request_10")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Dosage+Frequency&v=custom:(uuid,name,answers)")
              .headers(headers_1),
            http("request_11")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Dosage+Instructions&v=custom:(uuid,name,answers)")
              .headers(headers_1),
            http("request_12")
              .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Stopped+Order+Reason&v=custom:(uuid,name,answers)")
              .headers(headers_1),
            http("request_13")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty?property=mrs.genders")
              .headers(headers_1),
            http("request_14")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty?property=bahmni.relationshipTypeMap")
              .headers(headers_1),
            http("request_15")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty?property=bahmni.encounterType.default")
              .headers(headers_1),
            http("request_16")
              .get("/openmrs/ws/rest/v1/ordertype?v=custom:(uuid,display,conceptClasses:(uuid,display,name))")
              .headers(headers_1),
            http("request_17")
              .get("/openmrs/ws/rest/v1/location?operator=ALL&s=byTags&tags=Login+Location&v=default")
              .headers(headers_1),
            http("request_18")
              .get("/openmrs/ws/rest/v1/idgen/identifiertype")
              .headers(headers_1),
            http("request_19")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.activePatients&v=full")
              .headers(headers_1),
            http("request_20")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.activePatients&v=full")
              .headers(headers_1),
            http("request_21")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.todaysPatientsByProvider&v=full")
              .headers(headers_1),
            http("request_22")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.todaysPatients&v=full")
              .headers(headers_1),
            http("request_23")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.admittedPatients&v=full")
              .headers(headers_1),
            http("request_24")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.activePatients&v=full")
              .headers(headers_1),
            http("request_25")
              .get("/openmrs/ws/rest/v1/bahmnicore/sql?location_uuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&provider_uuid=cfdc8af5-5847-4ec4-ae85-6c81ed6d814a&q=emrapi.sqlSearch.activePatients&v=full")
              .headers(headers_1)))

    setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
      .assertions(global.successfulRequests.percent.is(100))
}