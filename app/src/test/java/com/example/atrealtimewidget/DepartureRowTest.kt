package com.example.atrealtimewidget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DepartureRowTest {
    private fun trip(
        departureTime: String = "14:37:00",
        stopHeadsign: String? = "Henderson via Grafton",
        tripHeadsign: String? = "Henderson"
    ) = StopTripAttributes(
        arrivalTime = departureTime,
        departureTime = departureTime,
        pickupType = 0,
        routeId = "WEST-201",
        stopHeadsign = stopHeadsign,
        tripHeadsign = tripHeadsign,
        tripId = "trip-1"
    )

    @Test
    fun displayTime_normalTime_dropsSeconds() {
        assertEquals("14:37", displayTime("14:37:00"))
    }

    @Test
    fun displayTime_afterMidnight_wrapsToClock() {
        assertEquals("00:07", displayTime("24:07:00"))
    }

    @Test
    fun displayTime_wellPastMidnight_wraps() {
        assertEquals("01:15", displayTime("25:15:00"))
    }

    @Test
    fun displayTime_unexpectedShape_returnedAsIs() {
        assertEquals("soon", displayTime("soon"))
    }

    @Test
    fun toDepartureRow_usesStopHeadsign() {
        val row = toDepartureRow(trip(), null)
        assertEquals("Henderson via Grafton", row.destination)
    }

    @Test
    fun toDepartureRow_noStopHeadsign_fallsBackToTripHeadsign() {
        val row = toDepartureRow(trip(stopHeadsign = null), null)
        assertEquals("Henderson", row.destination)
    }

    @Test
    fun toDepartureRow_noHeadsigns_saysUnknown() {
        val row = toDepartureRow(trip(stopHeadsign = null, tripHeadsign = null), null)
        assertEquals("Unknown destination", row.destination)
    }

    @Test
    fun toDepartureRow_withLiveDelay_describesIt() {
        val row = toDepartureRow(trip(), LiveStatus(cancelled = false, delaySeconds = 120))
        assertEquals(DepartureRow("14:37", "Henderson via Grafton", "2 min late"), row)
    }

    @Test
    fun toDepartureRow_noLiveData_saysScheduled() {
        val row = toDepartureRow(trip(), null)
        assertEquals("scheduled (no live data)", row.status)
    }

    @Test
    fun toDepartureRow_withPlatform_keepsIt() {
        val row = toDepartureRow(trip(), null, "1")
        assertEquals("1", row.platform)
    }

    @Test
    fun toDepartureRow_noPlatform_isNull() {
        val row = toDepartureRow(trip(), null, null)
        assertNull(row.platform)
    }

    @Test
    fun platformLabel_withPlatform_saysPl() {
        assertEquals("Pl. 1", platformLabel("1"))
    }

    @Test
    fun platformLabel_noPlatform_isEmpty() {
        assertEquals("", platformLabel(null))
    }

    @Test
    fun toDepartureRow_withRoute_keepsIt() {
        val row = toDepartureRow(trip(), null, null, "38")
        assertEquals("38", row.route)
    }

    @Test
    fun toDepartureRow_noRoute_isNull() {
        val row = toDepartureRow(trip(), null, "1")
        assertNull(row.route)
    }

    @Test
    fun departureText_withRoute_putsRouteAfterTime() {
        val row = DepartureRow("08:14", "ONEHUNGA", "on time", route = "38")
        assertEquals("08:14   38   ONEHUNGA   on time", departureText(row))
    }

    @Test
    fun departureText_noRoute_isSameAsBefore() {
        val row = DepartureRow("14:37", "Henderson via Grafton", "2 min late", "1")
        // The text the screen built before route labels were added.
        val before = "${row.time}   ${row.destination}   ${row.status}"
        assertEquals(before, departureText(row))
    }
}
