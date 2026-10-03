package com.example.atrealtimewidget

data class LiveStatus(val cancelled: Boolean, val delaySeconds: Int?)

fun liveStatusByTrip(response: TripUpdatesResponse): Map<String, LiveStatus> =
    (response.response?.entity ?: emptyList())
        .mapNotNull { it.tripUpdate }
        .associate { update ->
            update.trip.tripId to LiveStatus(
                cancelled = update.trip.scheduleRelationship == 3,
                delaySeconds = update.delay ?: update.stopTimeUpdate?.departure?.delay
            )
        }

fun describe(status: LiveStatus?): String = when {
    status == null -> "scheduled (no live data)"
    status.cancelled -> "CANCELLED"
    status.delaySeconds == null -> "scheduled (no live data)"
    status.delaySeconds >= 60 -> "${status.delaySeconds / 60} min late"
    status.delaySeconds <= -60 -> "${-status.delaySeconds / 60} min early"
    else -> "on time"
}