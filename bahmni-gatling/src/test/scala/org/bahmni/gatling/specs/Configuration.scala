package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Configuration {

  object Constants {
    val BASE_URL = "https://172.18.2.46"
    val LOGIN_USER = "superman"
    val LOGIN_USER_UUID = "12a02226-5bb1-4ad0-bcca-14e74e1f755b"
    val PROVIDER_UUID = "cfdc8af5-5847-4ec4-ae85-6c81ed6d814a"
    val LOGIN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"
    val PATIENT_IDENTIFIER = "BAH2530"
    val PATIENT_UUID = "54c01cae-78c6-11e6-917f-0ac3d00284a3"
    val VISIT_UUID = "473afb1d-5042-483c-a634-2da3023e2c04"
    val RADIOLOGY_ORDER_TYPE_UUID = "244b43be-28f1-11e4-86a0-005056822b0b"
    val USG_ORDER_TYPE_UUID = "c39840d9-57a1-11e6-8158-d4ae52d4c69b"
    var ENCOUNTER_TYPE_UUID = "24482b92-28f1-11e4-86a0-005056822b0b"
  }

  object HttpConf {
    val HTTP_PROTOCOL = http
      .baseURL(Configuration.Constants.BASE_URL)
      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
  }

  object Load {
    var USER_PROFILE = Iterable(constantUsersPerSec(1) during 1)
  }

}