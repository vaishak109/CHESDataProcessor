package com.example.transformation

import org.json.JSONObject
import kotlin.jvm.Throws
import kotlin.text.Regex
import java.util.UUID


class Transformer {

    @Throws(TransformationException :: class)
            /**
             *transform the input JSONObject to a preferred FHIR String template
             * @param npiid Kore patient ID
             * @param payloadTime time stamp in UTC W3C format "YYYY-MM-DDThh:mm:ssTZD"
             * @param inputJSON a json object representing a telemetry reading
             * @param template the FHIR template to which the telemetry reading is to be transformed
             * @return transformed output string which is a FHIR template with actual values
             * @throws TransformationException if the input JSONObject is invalid
             */
    fun transform(npiid: String, payloadTime: String, inputJSON: JSONObject, template: String): String {
        var transformedOutput = template

        //finding the *uuid[j]* (where j>=1) strings to be replaced using regex. The *uuid[j]* strings are then stored in a set uuidStrings.
        val regex = Regex("[*]uuid[0-9]+[*]")
        val uuidStrings = regex.findAll(transformedOutput).map { it.value }.toSet()

        // replacing *npiid* with actual npiid values
        transformedOutput = transformedOutput.replace("*npiid*", npiid)

        //replacing *payloadTime* with actual value of payloadTime
        transformedOutput = transformedOutput.replace("*payloadTime*", payloadTime)



        //getting the JSONArray containing the telemetry readings
        val readings = inputJSON.optJSONArray("telemetry") ?: throw TransformationException("Invalid input format, either the value of 'telemetry' key is not a JSONArray or the input JSONObject doesn't have the key 'telemetry'")
        val noOfReadings = readings.length()

        //looping through the individual readings which is a JSONObject
        for (index in 0 until noOfReadings) {
            val reading = readings.optJSONObject(index) ?: throw TransformationException("Invalid input format, either the value at index $index of the JSONArray with key 'telemetry' is null or the value is not a JSONObject")
            val binding = reading.optString("binding", null) ?: throw TransformationException("Invalid input format, the JSONObject inside the JSONArray doesn't have the key 'binding'")
            val value = reading.optString("value", null) ?: throw TransformationException("Invalid input format, the JSONObject inside the JSONArray doesn't have the key 'value'")

            //replacing the *binding* with actual values
            transformedOutput = transformedOutput.replace("*$binding*", value)
        }


        //replacing *uuid[j]* (where j >= 1) with actual values
        for (uuid in uuidStrings) {
            val newUUID = UUID.randomUUID().toString()
            transformedOutput = transformedOutput.replace(uuid, newUUID)
        }
        return transformedOutput
    }
}