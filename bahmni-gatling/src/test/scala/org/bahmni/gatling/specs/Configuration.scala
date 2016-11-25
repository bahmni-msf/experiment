package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

object Configuration {
    object Constants {
        val BASE_URL = "https://172.18.2.46"
        val LOGIN_USER = "superman"
        val LOGIN_USER_UUID = "12a02226-5bb1-4ad0-bcca-14e74e1f755b"
        val LOGIN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"
        val PATIENT_IDENTIFIER = "BAH2530"
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
        var USER_PROFILE = Iterable(constantUsersPerSec(1 ) during 1)
    }
}