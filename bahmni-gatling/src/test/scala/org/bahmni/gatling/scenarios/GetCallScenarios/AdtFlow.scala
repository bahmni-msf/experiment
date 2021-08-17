object AdtFlow{

        val goToADTApp:HttpRequestBuilder={
        getUser(LOGIN_USER)
        .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getProviderForUser(LOGIN_USER_UUID),
        getRegistrationConcepts,
        getPatientConfigFromServer,
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getIdentifierTypes,
        getAdmissionLocations,
        getPatientsInSearchTab(LOGIN_LOCATION_UUID,PROVIDER_UUID,"emrapi.sqlSearch.patientsToAdmit"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID,PROVIDER_UUID,"emrapi.sqlSearch.patientsToAdmit"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID,PROVIDER_UUID,"emrapi.sqlSearch.admittedPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID,PROVIDER_UUID,"emrapi.sqlSearch.patientsToDischarge"),
        getWardListDetails("Location-23"),
        getWardListDetails("Location-25")
        )
        }

        val scn:ScenarioBuilder=scenario("inPatientListing")
        .during(Configuration.Load.DURATION){
        exec(goToADTApp)
        }
        }
