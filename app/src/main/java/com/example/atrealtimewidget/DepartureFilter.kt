package com.example.atrealtimewidget

import java.util.Calendar

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3600
private const val HOURS_PER_DAY = 24
private const val SERVICE_DAY_START_HOUR = 4

// nowSeconds is the current time as seconds since the start of the service day (see secondsOf).
fun nextDepartures(trips: List<StopTripData>, nowSeconds: Int, count: Int): List<StopTripAttributes> =
    trips.map { it.attributes }
        .filter { it.pickupType == 0 } // skip trains that end here
        .filter { secondsOf(it.departureTime) >= nowSeconds } // skip trains that have already left
        .sortedBy { secondsOf(it.departureTime) }
        .take(count)

// "14:07:00" -> seconds since midnight. Trips after midnight use hours of 24 or more ("24:07:00").
fun secondsOf(time: String): Int {
    val (hours, minutes, seconds) = time.split(":").map { it.toInt() }
    return hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds
}

// Before 4am we're still in yesterday's service day, so 00:10 counts as 24:10.
fun serviceSecondsOf(now: Calendar): Int {
    val hour = now.get(Calendar.HOUR_OF_DAY)
    val serviceHour = if (hour < SERVICE_DAY_START_HOUR) hour + HOURS_PER_DAY else hour
    return serviceHour * SECONDS_PER_HOUR +
        now.get(Calendar.MINUTE) * SECONDS_PER_MINUTE +
        now.get(Calendar.SECOND)
}
