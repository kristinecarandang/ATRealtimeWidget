package com.example.atrealtimewidget

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PlatformLookupTest {
    private fun departure(tripId: String, stopId: String?) = StopTripAttributes(
        arrivalTime = "22:07:00",
        departureTime = "22:07:00",
        pickupType = 0,
        routeId = "O-W-201",
        stopHeadsign = "Henderson via Grafton",
        tripHeadsign = "Onehunga To Henderson 2 Via Newmarket 1",
        tripId = tripId,
        stopId = stopId
    )

    @Test
    fun sameStopId_isLookedUpOnce() = runBlocking {
        val lookedUp = mutableListOf<String>()
        val departures = listOf(departure("a", "9503-bcf68071"), departure("b", "9503-bcf68071"))

        val platforms = platformsByStop(departures) { stopId -> lookedUp.add(stopId); "1" }

        assertEquals(listOf("9503-bcf68071"), lookedUp)
        assertEquals(mapOf("9503-bcf68071" to "1"), platforms)
    }

    @Test
    fun differentStopIds_areEachLookedUp() = runBlocking {
        val departures = listOf(departure("a", "stop-1"), departure("b", "stop-2"))

        val platforms = platformsByStop(departures) { stopId -> if (stopId == "stop-1") "1" else "2" }

        assertEquals(mapOf("stop-1" to "1", "stop-2" to "2"), platforms)
    }

    @Test
    fun failedLookup_givesNoPlatform() = runBlocking {
        val departures = listOf(departure("a", "9503-bcf68071"))

        val platforms = platformsByStop(departures) { error("network down") }

        assertEquals(mapOf("9503-bcf68071" to null), platforms)
    }

    @Test
    fun departureWithoutStopId_isNotLookedUp() = runBlocking {
        val lookedUp = mutableListOf<String>()

        platformsByStop(listOf(departure("a", null))) { stopId -> lookedUp.add(stopId); "1" }

        assertEquals(emptyList<String>(), lookedUp)
    }
}
