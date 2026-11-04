package com.example.atrealtimewidget

import java.util.Locale

private const val HOURS_PER_DAY = 24

// One line on the departure screen. platform is null when the stop has no platform number.
// route is null when the departure has no route label (e.g. trains).
data class DepartureRow(
    val time: String,
    val destination: String,
    val status: String,
    val platform: String? = null,
    val route: String? = null
)

fun toDepartureRow(
    trip: StopTripAttributes,
    live: LiveStatus?,
    platform: String? = null,
    route: String? = null
): DepartureRow =
    DepartureRow(
        time = displayTime(trip.departureTime),
        destination = trip.stopHeadsign ?: trip.tripHeadsign ?: "Unknown destination",
        status = describe(live),
        platform = platform,
        route = route
    )

// The left-hand text of a row: time, route (if any), destination, status.
// With no route, this is "08:14   ONEHUNGA   on time".
fun departureText(row: DepartureRow): String =
    listOfNotNull(row.time, row.route, row.destination, row.status).joinToString("   ")

// "1" -> "Pl. 1". No platform -> empty text, so the column stays blank.
fun platformLabel(platform: String?): String = if (platform == null) "" else "Pl. $platform"

// AT times for trains after midnight run past 24:00 (e.g. "24:07:00").
// For display, we wrap them back to the normal clock, so that becomes "00:07".
fun displayTime(gtfsTime: String): String {
    val parts = gtfsTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull()
    val minute = parts.getOrNull(1)
    if (hour == null || minute == null) return gtfsTime
    return String.format(Locale.US, "%02d:%s", hour % HOURS_PER_DAY, minute)
}
