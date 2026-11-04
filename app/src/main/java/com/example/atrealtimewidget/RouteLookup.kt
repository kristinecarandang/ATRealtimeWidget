package com.example.atrealtimewidget

// The only GTFS route type that gets a route label on the board (3 = bus).
// Other types, such as 2 (rail), show no label.
private const val LABELLED_ROUTE_TYPE = 3

// The label to show for a route, or null for no label.
fun routeLabel(route: RouteAttributes?): String? =
    route?.takeIf { it.routeType == LABELLED_ROUTE_TYPE }?.routeShortName

// Looks up each different route ID once, even if several departures share it.
// A failed lookup means "no label" for that route, so the board still loads.
suspend fun routeLabelsByRoute(
    departures: List<StopTripAttributes>,
    lookupRoute: suspend (routeId: String) -> RouteAttributes?
): Map<String, String?> =
    departures.map { it.routeId }
        .distinct()
        .associateWith { routeId -> routeLabel(runCatching { lookupRoute(routeId) }.getOrNull()) }
