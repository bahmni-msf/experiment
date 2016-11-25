package org.bahmni.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object HttpRequests {

  def getUser(username: String): HttpRequestBuilder = {
    http("get user")
      .get("/openmrs/ws/rest/v1/user?v=custom:(username,uuid,person:(uuid,),privileges:(name,retired),userProperties)")
      .queryParam("username", username)
  }

  def getGlobalProperty(property: String): HttpRequestBuilder = {
    http("get " + property + " global property")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty")
      .queryParam("property", property)
  }

  def getProviderForUser(userUuid: String): HttpRequestBuilder = {
    http("get provider")
      .get("/openmrs/ws/rest/v1/provider")
      .queryParam("user", userUuid)
  }

  def getLoginLocations: HttpRequestBuilder = {
    http("get login locations")
      .get("/openmrs/ws/rest/v1/location?operator=ALL&s=byTags&tags=Login+Location&v=default")
  }

  def getAddressHierarchyLevel: HttpRequestBuilder = {
    http("get address hierarchy level")
      .get("/openmrs/module/addresshierarchy/ajax/getOrderedAddressHierarchyLevels.form")
  }

  def getVisitLocation(loginLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/bahmnicore/visitLocation/" + loginLocationUuid)
  }

  def getIdentifierTypes: HttpRequestBuilder = {
    http("get identifier types")
      .get("/openmrs/ws/rest/v1/idgen/identifiertype")
  }

  def getRelationshipTypes: HttpRequestBuilder = {
    http("get relationship types")
      .get("/openmrs/ws/rest/v1/relationshiptype?v=custom:(aIsToB,bIsToA,uuid)")
  }

  def getEntityMapping: HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=loginlocation_visittype&s=byEntityAndMappingType")
  }

  def getPersonAttributeTypes: HttpRequestBuilder = {
    http("get person attribute types")
      .get("/openmrs/ws/rest/v1/personattributetype?v=custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def getRegistrationConcepts: HttpRequestBuilder = {
    http("get registration concepts")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
  }

  def searchPatientUsingIdentifier(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("search patient " + identifier)
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("loginLocationUuid", loginLocationUuid)
      .queryParam("identifier", identifier)
      .queryParam("addressFieldName", "city_village")
      .queryParam("addressSearchResultsConfig", "city_village")
      .queryParam("addressSearchResultsConfig", "address1")
      .queryParam("filterOnAllIdentifiers", "true")
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }
}
