package com.example.atrealtimewidget

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AtApiServiceTest {

    @Test
    fun `parses stop JSON into StopResponse correctly`() {
        val sampleJson = """
            {
              "data": {
                "type": "stop",
                "id": "605-b9605c8e",
                "attributes": {
                  "location_type": 1.0,
                  "stop_code": "605",
                  "stop_id": "605-b9605c8e",
                  "stop_lat": -36.92539,
                  "stop_lon": 174.7868,
                  "stop_name": "Onehunga Train Station",
                  "vehicle_type": 2.0,
                  "wheelchair_boarding": 0.0
                }
              }
            }
        """.trimIndent()

        val result = Gson().fromJson(sampleJson, StopResponse::class.java)

        assertEquals("Onehunga Train Station", result.data.attributes.stopName)
        assertEquals("605-b9605c8e", result.data.id)
        assertEquals(-36.92539, result.data.attributes.stopLat, 0.0001)
    }

    // Real "Stop by id" response for the Onehunga platform.
    private val platformJson = """
        {"data":{"type":"stop","id":"9503-bcf68071","attributes":{"location_type":0,"parent_station":"605-b9605c8e","platform_code":"1","stop_code":"9503","stop_id":"9503-bcf68071","stop_lat":-36.92539,"stop_lon":174.7868,"stop_name":"Onehunga Train Station","vehicle_type":2,"wheelchair_boarding":0}}}
    """.trimIndent()

    @Test
    fun `stop with platform_code gives the platform`() {
        val result = Gson().fromJson(platformJson, StopResponse::class.java)
        assertEquals("1", result.data.attributes.platformCode)
    }

    @Test
    fun `stop without platform_code gives no platform`() {
        val json = platformJson.replace("\"platform_code\":\"1\",", "")
        val result = Gson().fromJson(json, StopResponse::class.java)
        assertNull(result.data.attributes.platformCode)
    }

    @Test
    fun `stoptrip keeps its stop_id`() {
        val json = """
            {"data":[{"type":"stoptrip","id":"stop:9503-bcf68071_trip:259-860001-79620-2-H1401012-9b573754","attributes":{"arrival_time":"22:07:00","departure_time":"22:07:00","direction_id":0,"drop_off_type":0,"pickup_type":0,"route_id":"O-W-201","service_date":"2026-11-03","shape_id":"259-860001-f60817d2","stop_headsign":"Henderson via Grafton","stop_id":"9503-bcf68071","stop_sequence":1,"trip_headsign":"Onehunga To Henderson 2 Via Newmarket 1","trip_id":"259-860001-79620-2-H1401012-9b573754","trip_start_time":"22:07:00"}}]}
        """.trimIndent()
        val result = Gson().fromJson(json, StopTripsResponse::class.java)
        assertEquals("9503-bcf68071", result.data[0].attributes.stopId)
    }
}
