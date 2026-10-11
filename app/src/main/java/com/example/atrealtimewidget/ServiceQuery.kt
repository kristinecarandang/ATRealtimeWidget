package com.example.atrealtimewidget

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// The timetable's "day" rolls over at 4am, and times past midnight run on from 24:00.
private const val SERVICE_DAY_START_HOUR = 4
private const val HOURS_PER_DAY = 24

data class ServiceQuery(val date: String, val startHour: Int)

// Early-morning hours still belong to the previous day's timetable,
// where times run past 24:00 (e.g. 24:07). The API also rejects hour 0.
fun serviceQueryFor(now: Calendar): ServiceQuery {
    val hour = now.get(Calendar.HOUR_OF_DAY)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return if (hour < SERVICE_DAY_START_HOUR) {
        val yesterday = now.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_MONTH, -1)
        ServiceQuery(formatter.format(yesterday.time), hour + HOURS_PER_DAY)
    } else {
        ServiceQuery(formatter.format(now.time), hour)
    }
}
