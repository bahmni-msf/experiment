package org.bahmni.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Configuration {

  object Constants {
    val BASE_HTTPS_URL = "https://172.18.2.46"
    val BASE_HTTP_URL = "http://172.18.2.46:8050"
    val LOGIN_USER = "superman"
    val LOGIN_USER_UUID = "12a02226-5bb1-4ad0-bcca-14e74e1f755b"
    val PROVIDER_UUID = "cfdc8af5-5847-4ec4-ae85-6c81ed6d814a"
    val LOGIN_LOCATION_UUID = "8abeebf1-0e95-4f0c-8553-a153bf1dc6e5"
    val PATIENT_IDENTIFIER = "BAH2530"
    val PATIENT_IDENTIFIER1 = "COM196196"
    val PATIENT_IDENTIFIER2 = "BAH196772"
    val PATIENT_UUID = "2e1a2899-409c-40c4-b8fd-3476bb11dce3"
    val VISIT_UUID = "96ef0b79-a6b1-4857-9faf-22b421e8fb06"
    val ANOTHER_PATIENT_UUID = "33cd0c72-3c9a-47be-ad17-e3324b7c8f82"
    val ANOTHER_VISIT_UUID = "bb7b5bcf-c875-4fd6-9a05-e2896a3c8b2e"
    val RADIOLOGY_ORDER_TYPE_UUID = "244b43be-28f1-11e4-86a0-005056822b0b"
    val USG_ORDER_TYPE_UUID = "c39840d9-57a1-11e6-8158-d4ae52d4c69b"
    var ENCOUNTER_TYPE_UUID = "24482b92-28f1-11e4-86a0-005056822b0b"
    var ATOMFEED_ENCOUNTER_UUID = "1b5e768d-3c07-4bfb-8195-cc1f768d29d6"
    val ALL_TESTS_AND_PANELS ="24d98284-28f1-11e4-86a0-005056822b0b"
  }

  object HttpConf {
    val HTTPS_PROTOCOL = http
      .baseURL(Configuration.Constants.BASE_HTTPS_URL)

      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
    val HTTP_PROTOCOL = http
      .baseURL("http://172.18.2.46:8050")

      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
  }

  object Load {
    var ATOMFEED_USER_PROFILE = rampUsers(3) over 10
    var DURATION = 3600
  }

}