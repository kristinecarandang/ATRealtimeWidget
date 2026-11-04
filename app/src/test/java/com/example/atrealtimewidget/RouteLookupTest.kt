package com.example.atrealtimewidget

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RouteLookupTest {
    private fun departure(tripId: String, routeId: String) = StopTripAttributes(
        arrivalTime = "08:14:00",
        departureTime = "08:14:00",
        pickupType = 0,
        routeId = routeId,
        stopHeadsign = "ONEHUNGA",
        tripHeadsign = "Airport to Onehunga via Mangere Town Centre",
        tripId = tripId,
        stopId = "2419-9c55e172"
    )

    private val busRoute = RouteAttributes(routeShortName = "38", routeType = 3)

    @Test
    fun routeLabel_busRoute_givesShortName() {
        assertEquals("38", routeLabel(busRoute))
    }

    @Test
    fun routeLabel_railRoute_givesNoLabel() {
        assertNull(routeLabel(RouteAttributes(routeShortName = "ONE", routeType = 2)))
    }

    @Test
    fun routeLabel_missingRoute_givesNoLabel() {
        assertNull(routeLabel(null))
    }

    @Test
    fun routeLabel_missingShortName_givesNoLabel() {
        assertNull(routeLabel(RouteAttributes(routeShortName = null, routeType = 3)))
    }

    @Test
    fun routeLabel_missingRouteType_givesNoLabel() {
        assertNull(routeLabel(RouteAttributes(routeShortName = "38", routeType = null)))
    }

    @Test
    fun sameRouteId_isLookedUpOnce() = runBlocking {
        val lookedUp = mutableListOf<String>()
        val departures = listOf(departure("a", "38-203"), departure("b", "38-203"))

        val labels = routeLabelsByRoute(departures) { routeId -> lookedUp.add(routeId); busRoute }

        assertEquals(listOf("38-203"), lookedUp)
        assertEquals(mapOf("38-203" to "38"), labels)
    }

    @Test
    fun differentRouteIds_areEachLookedUp() = runBlocking {
        val lookedUp = mutableListOf<String>()
        val departures = listOf(departure("a", "38-203"), departure("b", "380-203"))

        val labels = routeLabelsByRoute(departures) { routeId ->
            lookedUp.add(routeId)
            if (routeId == "38-203") busRoute else RouteAttributes(routeShortName = "380", routeType = 3)
        }

        assertEquals(listOf("38-203", "380-203"), lookedUp)
        assertEquals(mapOf("38-203" to "38", "380-203" to "380"), labels)
    }

    @Test
    fun failedLookup_givesNoLabel() = runBlocking {
        val labels = routeLabelsByRoute(listOf(departure("a", "38-203"))) { error("network down") }

        assertEquals(mapOf("38-203" to null), labels)
    }

    @Test
    fun railRoute_givesNoLabel() = runBlocking {
        val departures = listOf(departure("a", "ONE-201"))

        val labels = routeLabelsByRoute(departures) { RouteAttributes(routeShortName = "ONE", routeType = 2) }

        assertEquals(mapOf("ONE-201" to null), labels)
    }

    @Test
    fun busDeparture_rowGetsLabel() = runBlocking {
        val trip = departure("a", "38-203")
        val labels = routeLabelsByRoute(listOf(trip)) { busRoute }

        val row = toDepartureRow(trip, null, null, labels[trip.routeId])

        assertEquals("38", row.route)
    }

    @Test
    fun failedLookup_rowStillBuiltWithoutLabel() = runBlocking {
        val trip = departure("a", "38-203")
        val labels = routeLabelsByRoute(listOf(trip)) { error("network down") }

        val row = toDepartureRow(trip, null, null, labels[trip.routeId])

        assertEquals(DepartureRow("08:14", "ONEHUNGA", "scheduled (no live data)"), row)
    }
}
