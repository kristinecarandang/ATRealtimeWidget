package com.example.atrealtimewidget

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class DepartureFilterTest {

    private fun trip(id: String, time: String, pickupType: Int) = """
        {"type":"stoptrip","id":"x-$id","attributes":{
          "arrival_time":"$time","departure_time":"$time","pickup_type":$pickupType,
          "route_id":"O-W-201","stop_headsign":"Henderson via Grafton",
          "trip_headsign":"Onehunga To Henderson 2 Via Newmarket 1","trip_id":"$id"}}
    """.trimIndent()

    private fun parse(vararg trips: String): StopTripsResponse =
        Gson().fromJson("""{"data":[${trips.joinToString(",")}]}""", StopTripsResponse::class.java)

    @Test
    fun json_isParsedIntoStopTrips() {
        val result = parse(trip("t1", "14:07:00", 0))
        assertEquals("t1", result.data[0].attributes.tripId)
        assertEquals("14:07:00", result.data[0].attributes.departureTime)
    }

    @Test
    fun tripsEndingHere_areRemoved() {
        val result = parse(trip("ends", "14:01:00", 1), trip("leaves", "14:07:00", 0))
        assertEquals(listOf("leaves"), nextDepartures(result.data, 5).map { it.tripId })
    }

    @Test
    fun pastMidnightTimes_sortAfterLateEvening() {
        val result = parse(trip("late", "24:07:00", 0), trip("evening", "23:37:00", 0))
        assertEquals(listOf("evening", "late"), nextDepartures(result.data, 5).map { it.tripId })
    }

    @Test
    fun result_isLimitedToRequestedCount() {
        val result = parse(trip("a", "10:00:00", 0), trip("b", "10:10:00", 0), trip("c", "10:20:00", 0))
        assertEquals(2, nextDepartures(result.data, 2).size)
    }
}