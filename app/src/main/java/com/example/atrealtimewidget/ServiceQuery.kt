package com.example.atrealtimewidget

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ServiceQuery(val date: String, val startHour: Int)

// Early-morning hours still belong to the previous day's timetable,
// where times run past 24:00 (e.g. 24:07). The API also rejects hour 0.
fun serviceQueryFor(now: Calendar): ServiceQuery {
    val hour = now.get(Calendar.HOUR_OF_DAY)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return if (hour < 4) {
        val yesterday = now.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_MONTH, -1)
        ServiceQuery(formatter.format(yesterday.time), hour + 24)
    } else {
        ServiceQuery(formatter.format(now.time), hour)
    }
}