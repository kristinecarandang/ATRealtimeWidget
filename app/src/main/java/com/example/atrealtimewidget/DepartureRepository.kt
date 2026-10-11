package com.example.atrealtimewidget

import android.util.Log
import java.util.Calendar

private const val TAG = "AtApi"
private const val DEPARTURES_TO_SHOW = 3
private const val HOUR_RANGE = 2

// Everything the screen needs: the station name and the next few departures.
data class DepartureBoard(val stopName: String, val rows: List<DepartureRow>)

// Returns null if anything goes wrong.
// Temporary catch-all: task 10 replaces this with specific, tested error handling.
@Suppress("TooGenericExceptionCaught")
suspend fun loadDepartureBoard(stopId: String): DepartureBoard? {
    var step = "station lookup"
    return try {
        val stop = AtApiClient.service.getStop(
            stopId = stopId,
            apiKey = BuildConfig.AT_API_KEY
        )

        step = "stop trips"
        val query = serviceQueryFor(Calendar.getInstance())
        val trips = AtApiClient.service.getStopTrips(
            stopId = stopId,
            date = query.date,
            startHour = query.startHour,
            hourRange = HOUR_RANGE,
            apiKey = BuildConfig.AT_API_KEY
        )
        val next = nextDepartures(trips.data, DEPARTURES_TO_SHOW)

        step = "live trip updates"
        val live = if (next.isEmpty()) emptyMap() else liveStatusByTrip(
            AtApiClient.service.getTripUpdates(
                tripIds = next.joinToString(",") { it.tripId },
                apiKey = BuildConfig.AT_API_KEY
            )
        )

        val board = DepartureBoard(
            stopName = stop.data.attributes.stopName,
            rows = next.map { toDepartureRow(it, live[it.tripId]) }
        )
        board.rows.forEach { Log.d(TAG, "Departs ${it.time} to ${it.destination}: ${it.status}") }
        board
    } catch (e: Exception) {
        Log.e(TAG, "Failed during $step: ${e.message}")
        null
    }
}
