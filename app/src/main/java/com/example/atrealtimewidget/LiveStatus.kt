package com.example.atrealtimewidget

private const val SECONDS_PER_MINUTE = 60

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
    status.delaySeconds >= SECONDS_PER_MINUTE -> "${status.delaySeconds / SECONDS_PER_MINUTE} min late"
    status.delaySeconds <= -SECONDS_PER_MINUTE -> "${-status.delaySeconds / SECONDS_PER_MINUTE} min early"
    else -> "on time"
}
