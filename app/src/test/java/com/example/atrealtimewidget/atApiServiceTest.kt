package com.example.atrealtimewidget

import com.google.gson.Gson
import org.junit.Assert.assertEquals
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
}
