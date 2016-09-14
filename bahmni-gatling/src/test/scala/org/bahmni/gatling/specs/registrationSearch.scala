package org.bahmni.gatling.spec

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class registrationSearch extends Simulation {

	val headers_0 = Map(
		"Cache-Control" -> "no-cache",
		"Disable-WWW-Authenticate" -> "true",
		"Pragma" -> "no-cache")

	val scn = scenario("registrationSearch")
		.exec(http("regular_search")
              .get("/openmrs/ws/rest/v1/bahmnicore/search/patient?addressFieldValue=&addressSearchResultsConfig=%7B%7D&customAttribute=&loginLocationUuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&patientSearchResultsConfig=%7B%7D&programAttributeFieldValue=&q=%25&s=byIdOrNameOrVillage&startIndex=0")
              .headers(headers_0)
          .resources(http("exact_search")
			.get("/openmrs/ws/rest/v1/bahmnicore/search/patient?addressSearchResultsConfig=%7B%7D&filterOnAllIdentifiers=false&identifier=BAH253020&loginLocationUuid=8d6c993e-c2cc-11de-8d13-0010c6dffd0f&patientSearchResultsConfig=%7B%7D&programAttributeFieldValue=&s=byIdOrNameOrVillage&startIndex=0")
			.headers(headers_0)))

	setUp(scn.inject(Configuration.Load.USER_PROFILE)).protocols(Configuration.HttpConf.HTTP_PROTOCOL)
		.assertions(global.successfulRequests.percent.is(100))
}
