package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration.HttpConf._
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.scenarios._

class UserSimulation extends Simulation {

  setUp(
    RegistrationSearch.scn.inject(rampUsers(3) over 10).protocols(HTTPS_PROTOCOL),
    ClinicalFlow.scn.inject(rampUsers(20) over 20).protocols(HTTPS_PROTOCOL),
    PatientImage.scn.inject(splitUsers(1200) into(rampUsers(60) over 5) separatedBy(11)).protocols(HTTP_PROTOCOL),
    AdtFlow.scn.inject(rampUsers(2) over 10).protocols(HTTPS_PROTOCOL),
    AtomfeedScenarios.patientFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.patientFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    BedManagementFlow.scn.inject(rampUsers(1) over 20).protocols(HTTPS_PROTOCOL),
      MedicationsFiller.scn.inject(rampUsers(1) over 20).protocols(HTTPS_PROTOCOL)
  )
    .assertions(global.successfulRequests.percent.gte(90))
}
