package com.example.transformation

import androidx.constraintlayout.widget.ConstraintSet
import org.junit.Test
import kotlin.test.assertFailsWith
import org.json.JSONObject
import kotlin.text.Regex

class TransformerUnitTest {

    private val transformer = Transformer()
    private val reUUID = Regex("[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}")


    @Test
    fun `When inputJSON Object is empty, throws TransformationException`() {
        val inputJson = JSONObject()
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When inputJSON Object does not have "telemetry" key, throws TransformationException`() {

        //                                  telemetry key is misspelled as tele
        val inputJson = JSONObject("{\"tele\":[{\"value\":94.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When "telemetry" array is an empty array, throws TransformationException`() {

        //                                  telemetry array is an empty array
        val inputJson = JSONObject("{\"telemetry\":[]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When telemetry array doesn't have an object with "binding" key, throws TransformationException`() {

        //    scroll towards right                                                                                          binding key is misspelled as bind
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":94.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"bind\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When the value of "binding" key is an empty string, throws TransformationException`() {

        //      scroll towards right                                                                               value of binding key is an empty string
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":94.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When the value of "binding" key is null, throws TransformationException`() {

        //     scroll towards right                                                                                value of binding key is null
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":94.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":null},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When telemetry array doesn't have an object with "value" key, throws TransformationException`() {

        //                                                  value key is misspelled as val
        val inputJson = JSONObject("{\"telemetry\":[{\"val\":94.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }

    @Test
    fun `When the value of "value" key is null, throws TransformationException`() {

        //                                                  value of "value" key is null
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":null,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        assertFailsWith<TransformationException>(
            message = "No exception found",
            block = {
                transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
            }
        )
    }


    @Test
    fun `When transform() is called it returns a string in which every *npiid* is replaced with the actual npiid`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        assert(transformedThermometerOutput.contains(NPIID) && !transformedThermometerOutput.contains("*npiid*"))
    }

    @Test
    fun `When transform() is called it returns a string in which every *payloadTime* is replaced with the actual payload time`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        assert(transformedThermometerOutput.contains(PAYLOADTIME) && !transformedThermometerOutput.contains("*payloadTime*"))
    }

    @Test
    fun `When transform() is called it returns a string in which *obs1* is replaced with the actual value of thermometer reading`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        assert(transformedThermometerOutput.contains("\"value\": 96.5") && !transformedThermometerOutput.contains("*obs1*"))
    }

    @Test
    fun `When transform() is called it returns a string in which *timestamp* is replaced with the actual value of timestamp`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        assert(transformedThermometerOutput.contains("2021-05-08T10:57:34+01:00") && !transformedThermometerOutput.contains("*timestamp*"))
    }

    @Test
    fun `When transform() is called it returns a string in which all *uuid1* is replaced with the actual value of uuid1 for thermometer`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        assert(reUUID.containsMatchIn(transformedThermometerOutput) && !transformedThermometerOutput.contains("*uuid1*"))
    }

    @Test
    fun `Only one uuid is generated for thermometer`() {
        val inputJson = JSONObject("{\"telemetry\":[{\"value\":96.5,\"unit\":\"fahrenheit\",\"name\":\"Temperature\",\"binding\":\"obs1\"},{\"value\":\"2021-05-08T10:57:34+01:00\",\"name\":\"Timestamp\",\"binding\":\"timestamp\"}]}")
        val transformedThermometerOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, THERMOMETER_TEMPLATE)
        val matches = reUUID.findAll(transformedThermometerOutput).map { it.value }.toSet()
        assert(matches.size == 1)
    }

    @Test
    fun `When transform() is called it returns a string in which all *uuid1*, *uuid2* and *uuid3* is replaced with the actual values of uuids for Pulse Oximeter`() {
        val inputJson = JSONObject("{\"telemetry\": [{\"value\": 98, \"unit\": \"%\", \"name\": \"SpO2\", \"binding\": \"obs1\"}, {\"value\": 80, \"unit\": \"BPM\", \"name\": \"PulseRate\", \"binding\": \"obs2\"}, {\"value\": 80, \"unit\": \"%\", \"name\": \"PulseAmplitudeIndex\", \"binding\": \"obs3\"}, {\"value\": \"2021-05-08T10:57:34+01:00\", \"name\": \"Timestamp\", \"binding\": \"timestamp\"}]}")
        val transformedPulseOximeterOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, PULSE_OXIMETER_TEMPLATE)
        assert(reUUID.containsMatchIn(transformedPulseOximeterOutput))
        assert(!transformedPulseOximeterOutput.contains(Regex("[*]uuid[0-9]+[*]")))
    }

    @Test
    fun `Only 3 distinct uuids are generated for Pulse Oximeter`() {
        val inputJson = JSONObject("{\"telemetry\": [{\"value\": 98, \"unit\": \"%\", \"name\": \"SpO2\", \"binding\": \"obs1\"}, {\"value\": 80, \"unit\": \"BPM\", \"name\": \"PulseRate\", \"binding\": \"obs2\"}, {\"value\": 80, \"unit\": \"%\", \"name\": \"PulseAmplitudeIndex\", \"binding\": \"obs3\"}, {\"value\": \"2021-05-08T10:57:34+01:00\", \"name\": \"Timestamp\", \"binding\": \"timestamp\"}]}")
        val transformedPulseOximeterOutput = transformer.transform(NPIID, PAYLOADTIME, inputJson, PULSE_OXIMETER_TEMPLATE)
        val matches = reUUID.findAll(transformedPulseOximeterOutput).map { it.value }.toSet()
        assert(matches.size == 3)
    }

    companion object {
        const val NPIID = "P0001"
        const val PAYLOADTIME = "2021-06-25T19:39:13+00:00"
        const val THERMOMETER_TEMPLATE = "{\"resourceType\": \"Bundle\", \"type\": \"transaction\", \"timestamp\": \"*payloadTime*\", \"entry\": [{\"fullUrl\": \"*npiid*\", \"resource\": {\"resourceType\": \"Patient\", \"id\": \"*npiid*\", \"identifier\": [{\"use\": \"usual\", \"value\": \"*npiid*\", \"assigner\": {\"display\": \"CHES RPM Client Name\"}}]}, \"request\": {\"method\": \"PUT\", \"url\": \"Patient/*npiid*\"}}, {\"fullUrl\": \"*deviceId*\", \"resource\": {\"id\": \"*deviceId*\", \"resourceType\": \"Device\", \"manufacturer\": \"ForaCare\", \"serialNumber\": \"5201100050\", \"deviceName\": [{\"name\": \"ForaCare Ear Thermometer IR20b\", \"type\": \"model-name\"}], \"modelNumber\": \"ForaCare IR20b\", \"type\": {\"coding\": [{\"system\": \"http://snomed.info/sct%22\", \"code\": \"700643004\", \"display\": \"Infrared patient thermometer, ear \"}]}}, \"request\": {\"method\": \"PUT\", \"url\": \"Device/*deviceId*\"}}, {\"fullUrl\": \"*uuid1*\", \"resource\": {\"id\": \"*uuid1*\", \"resourceType\": \"Observation\", \"status\": \"final\", \"category\": [{\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\", \"code\": \"vital-signs\", \"display\": \"Vital Signs\"}], \"text\": \"CHES Vital Signs\"}], \"code\": {\"coding\": [{\"system\": \"http://loinc.org\", \"code\": \"8310-5\", \"display\": \"Body Temperature\"}]}, \"effectiveDateTime\": \"*timestamp*\", \"performer\": [{\"reference\": \"Patient/*npiid*\"}], \"valueQuantity\": {\"code\": \"[degF]\", \"unit\": \"degree Fahrenheit\", \"value\": *obs1*, \"system\": \"http://unitsofmeasure.org\"}, \"device\": {\"display\": \"ForaCare Ear Thermometer IR20b\", \"reference\": \"Device/*deviceId*\"}}, \"request\": {\"method\": \"POST\", \"url\": \"Observation/*uuid1*\"}}]}"
        const val PULSE_OXIMETER_TEMPLATE = "{\"resourceType\": \"Bundle\", \"type\": \"transaction\", \"timestamp\": \"*payloadTime*\", \"entry\": [{\"fullUrl\": \"*npiid*\", \"resource\": {\"resourceType\": \"Patient\", \"id\": \"*npiid*\", \"identifier\": [{\"use\": \"usual\", \"value\": \"*npiid*\", \"assigner\": {\"display\": \"CHES RPM Client Name\"}}]}, \"request\": {\"method\": \"PUT\", \"url\": \"Patient/*npiid*\"}}, {\"fullUrl\": \"*deviceId*\", \"resource\": {\"id\": \"*deviceId*\", \"resourceType\": \"Device\", \"manufacturer\": \"Nonin 3230\", \"serialNumber\": \"5201100050\", \"deviceName\": [{\"name\": \"(Sp02)/Heart rate\", \"type\": \"model-name\"}], \"modelNumber\": \"320\", \"type\": {\"coding\": [{\"system\": \"http://snomed.info/sct%22\", \"code\": \"448703006\", \"display\": \"Pulse oximeter\"}]}}, \"request\": {\"method\": \"PUT\", \"url\": \"Device/*deviceId*\"}}, {\"fullUrl\": \"*uuid1*\", \"resource\": {\"id\": \"*uuid1*\", \"resourceType\": \"Observation\", \"status\": \"final\", \"category\": [{\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\", \"code\": \"vital-signs\", \"display\": \"Vital Signs\"}], \"text\": \"CHES Vital Signs\"}], \"code\": {\"coding\": [{\"system\": \"http://loinc.org\", \"code\": \"59408-5\", \"display\": \"Oxygen saturation in Arterial blood by Pulse oximetry\"}]}, \"effectiveDateTime\": \"*timestamp*\", \"performer\": [{\"reference\": \"Patient/*npiid*\"}], \"valueQuantity\": {\"value\": *obs1*, \"unit\": \"%\", \"system\": \"http://unitsofmeasure.org\", \"code\": \"%\"}, \"device\": {\"display\": \"Nonin 3230\", \"reference\": \"Device/*deviceId*\"}}, \"request\": {\"method\": \"POST\", \"url\": \"Observation/*uuid1*\"}}, {\"fullUrl\": \"*uuid2*\", \"resource\": {\"id\": \"*uuid2*\", \"resourceType\": \"Observation\", \"status\": \"final\", \"category\": [{\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\", \"code\": \"vital-signs\", \"display\": \"Vital Signs\"}], \"text\": \"CHES Vital Signs\"}], \"code\": {\"coding\": [{\"system\": \"http://loinc.org\", \"code\": \"8867-4\", \"display\": \"Heart rate\"}]}, \"effectiveDateTime\": \"*timestamp*\", \"performer\": [{\"reference\": \"Patient/*npiid*\"}], \"valueQuantity\": {\"value\": *obs2*, \"unit\": \"beats/minute\", \"system\": \"http://unitsofmeasure.org\", \"code\": \"/min\"}, \"device\": {\"display\": \"Nonin 3230\", \"reference\": \"Device/*deviceId*\"}}, \"request\": {\"method\": \"POST\", \"url\": \"Observation/*uuid2*\"}}, {\"fullUrl\": \"*uuid3*\", \"resource\": {\"id\": \"*uuid3*\", \"resourceType\": \"Observation\", \"status\": \"final\", \"category\": [{\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/observation-category\", \"code\": \"vital-signs\", \"display\": \"Vital Signs\"}], \"text\": \"CHES Vital Signs\"}], \"code\": {\"coding\": [{\"system\": \"http://loinc.org\", \"code\": \"61006-3\", \"display\": \"Perfusion index Tissue by Pulse oximetry\"}]}, \"effectiveDateTime\": \"*timestamp*\", \"performer\": [{\"reference\": \"Patient/*npiid*\"}], \"valueQuantity\": {\"value\": *obs3*, \"unit\": \"percent\", \"system\": \"http://unitsofmeasure.org\", \"code\": \"%\"}, \"device\": {\"display\": \"Nonin 3230\", \"reference\": \"Device/*deviceId*\"}}, \"request\": {\"method\": \"POST\", \"url\": \"Observation/*uuid3*\"}}]}"
    }
}