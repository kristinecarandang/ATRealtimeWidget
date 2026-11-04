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

    // Real "Route by id" response for route 38.
    private val routeJson = """
        {"data":{"type":"route","id":"38-203","attributes":{"agency_id":"RTH","route_id":"38-203","route_long_name":"38","route_short_name":"38","route_type":3}}}
    """.trimIndent()

    @Test
    fun `route JSON gives short name and type`() {
        val result = Gson().fromJson(routeJson, RouteResponse::class.java)
        assertEquals("38", result.data.attributes.routeShortName)
        assertEquals(3, result.data.attributes.routeType)
    }

    @Test
    fun `route without route_short_name gives no short name`() {
        val json = routeJson.replace("\"route_short_name\":\"38\",", "")
        val result = Gson().fromJson(json, RouteResponse::class.java)
        assertNull(result.data.attributes.routeShortName)
    }

    @Test
    fun `route without route_type gives no type`() {
        val json = routeJson.replace(",\"route_type\":3", "")
        val result = Gson().fromJson(json, RouteResponse::class.java)
        assertNull(result.data.attributes.routeType)
    }

    @Test
    fun `bus stop without platform_code gives no platform`() {
        // Real Mangere College stop, wrapped like a "Stop by id" response.
        val json = """
            {"data":{"type":"stop","id":"2419-9c55e172","attributes":{"location_type":0,"stop_code":"2419","stop_id":"2419-9c55e172","stop_lat":-36.95938,"stop_lon":174.79171,"stop_name":"Mangere College","vehicle_type":3,"wheelchair_boarding":0}}}
        """.trimIndent()
        val result = Gson().fromJson(json, StopResponse::class.java)
        assertEquals("Mangere College", result.data.attributes.stopName)
        assertNull(result.data.attributes.platformCode)
    }

    @Test
    fun `bus stoptrip keeps its route_id and headsign`() {
        val json = """
            {"data":[{"type":"stoptrip","id":"stop:2419-9c55e172_trip:1396-03811-27900-2-755db413","attributes":{"arrival_time":"08:14:00","departure_time":"08:14:00","direction_id":0,"drop_off_type":0,"pickup_type":0,"route_id":"38-203","service_date":"2026-11-04","shape_id":"1396-03811-c51751bc","stop_headsign":"ONEHUNGA","stop_id":"2419-9c55e172","stop_sequence":18,"trip_headsign":"Airport to Onehunga via Mangere Town Centre","trip_id":"1396-03811-27900-2-755db413","trip_start_time":"07:45:00"}}]}
        """.trimIndent()
        val result = Gson().fromJson(json, StopTripsResponse::class.java)
        assertEquals("38-203", result.data[0].attributes.routeId)
        assertEquals("ONEHUNGA", result.data[0].attributes.stopHeadsign)
    }
}
