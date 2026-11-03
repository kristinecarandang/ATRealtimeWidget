package com.example.atrealtimewidget

// Looks up the platform for each different stop ID once, even if several departures share it.
// A failed lookup means "no platform" for that stop, so the board still loads.
suspend fun platformsByStop(
    departures: List<StopTripAttributes>,
    lookupPlatform: suspend (stopId: String) -> String?
): Map<String, String?> =
    departures.mapNotNull { it.stopId }
        .distinct()
        .associateWith { stopId -> runCatching { lookupPlatform(stopId) }.getOrNull() }
