package com.example.atrealtimewidget

import java.util.Locale

private const val HOURS_PER_DAY = 24

// One line on the departure screen.
data class DepartureRow(val time: String, val destination: String, val status: String)

fun toDepartureRow(trip: StopTripAttributes, live: LiveStatus?): DepartureRow =
    DepartureRow(
        time = displayTime(trip.departureTime),
        destination = trip.stopHeadsign ?: trip.tripHeadsign ?: "Unknown destination",
        status = describe(live)
    )

// AT times for trains after midnight run past 24:00 (e.g. "24:07:00").
// For display, we wrap them back to the normal clock, so that becomes "00:07".
fun displayTime(gtfsTime: String): String {
    val parts = gtfsTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull()
    val minute = parts.getOrNull(1)
    if (hour == null || minute == null) return gtfsTime
    return String.format(Locale.US, "%02d:%s", hour % HOURS_PER_DAY, minute)
}
