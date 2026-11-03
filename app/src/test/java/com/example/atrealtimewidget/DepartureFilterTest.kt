package com.example.atrealtimewidget

import com.google.gson.Gson
import java.util.Calendar
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
        assertEquals(listOf("leaves"), nextDepartures(result.data, 0, 5).map { it.tripId })
    }

    @Test
    fun pastMidnightTimes_sortAfterLateEvening() {
        val result = parse(trip("late", "24:07:00", 0), trip("evening", "23:37:00", 0))
        assertEquals(listOf("evening", "late"), nextDepartures(result.data, 0, 5).map { it.tripId })
    }

    @Test
    fun result_isLimitedToRequestedCount() {
        val result = parse(trip("a", "10:00:00", 0), trip("b", "10:10:00", 0), trip("c", "10:20:00", 0))
        assertEquals(2, nextDepartures(result.data, 0, 2).size)
    }

    @Test
    fun trainsThatHaveLeft_areSkipped() {
        val result = parse(
            trip("t1407", "14:07:00", 0), trip("t1437", "14:37:00", 0), trip("t1507", "15:07:00", 0),
            trip("t1537", "15:37:00", 0), trip("t1607", "16:07:00", 0)
        )
        val next = nextDepartures(result.data, secondsOf("14:56:00"), 3)
        assertEquals(listOf("t1507", "t1537", "t1607"), next.map { it.tripId })
    }

    @Test
    fun afterMidnight_trainsThatHaveLeft_areSkipped() {
        val result = parse(
            trip("t2350", "23:50:00", 0), trip("t2405", "24:05:00", 0),
            trip("t2420", "24:20:00", 0), trip("t2435", "24:35:00", 0)
        )
        val next = nextDepartures(result.data, secondsOf("24:10:00"), 3)
        assertEquals(listOf("t2420", "t2435"), next.map { it.tripId })
    }

    @Test
    fun serviceSeconds_afternoon_isNormalClockTime() {
        assertEquals(secondsOf("14:56:00"), serviceSecondsOf(at(14, 56)))
    }

    @Test
    fun serviceSeconds_justAfterMidnight_countsAsHour24() {
        assertEquals(secondsOf("24:10:00"), serviceSecondsOf(at(0, 10)))
    }

    private fun at(hour: Int, minute: Int): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
}
