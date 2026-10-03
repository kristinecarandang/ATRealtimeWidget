package com.example.atrealtimewidget

fun nextDepartures(trips: List<StopTripData>, count: Int): List<StopTripAttributes> =
    trips.map { it.attributes }
        .filter { it.pickupType == 0 } // skip trains that end here
        .sortedBy { it.departureTime }
        .take(count)