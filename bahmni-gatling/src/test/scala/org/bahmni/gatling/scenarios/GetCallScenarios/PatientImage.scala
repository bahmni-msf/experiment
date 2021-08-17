object PatientImage{

private def fetchPatientImage:ChainBuilder={
        exec(getPatientImage)
        }

        val scn:ScenarioBuilder=scenario("fetch patient image")
        .during(Configuration.Load.DURATION){
        exec(fetchPatientImage)
        .pause(100)
        }
        }
