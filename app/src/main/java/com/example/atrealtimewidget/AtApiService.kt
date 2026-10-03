package com.example.atrealtimewidget
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

interface AtApiService {
    @GET("gtfs/v3/stops/{stopId}")
    suspend fun getStop(
        @Path("stopId") stopId: String,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): StopResponse

    @GET("gtfs/v3/stops/{stopId}/stoptrips")
    suspend fun getStopTrips(
        @Path("stopId") stopId: String,
        @Query("filter[date]") date: String,
        @Query("filter[start_hour]") startHour: Int,
        @Query("filter[hour_range]") hourRange: Int,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): StopTripsResponse

    @GET("realtime/legacy/tripupdates")
    suspend fun getTripUpdates(
        @Query("tripid", encoded = true) tripIds: String,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): TripUpdatesResponse
}

data class StopAttributes(
    @SerializedName("location_type") val locationType: Double,
    @SerializedName("stop_code") val stopCode: String,
    @SerializedName("stop_id") val stopId: String,
    @SerializedName("stop_lat") val stopLat: Double,
    @SerializedName("stop_lon") val stopLon: Double,
    @SerializedName("stop_name") val stopName: String,
    @SerializedName("vehicle_type") val vehicleType: Double,
    @SerializedName("wheelchair_boarding") val wheelchairBoarding: Double
)
data class StopResponse(
    val data: StopData
)

data class StopData(
    val type: String,
    val id: String,
    val attributes: StopAttributes
)

data class StopTripsResponse(
    val data: List<StopTripData>
)

data class StopTripData(
    val type: String,
    val id: String,
    val attributes: StopTripAttributes
)

data class StopTripAttributes(
    @SerializedName("arrival_time") val arrivalTime: String,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("pickup_type") val pickupType: Int,
    @SerializedName("route_id") val routeId: String,
    @SerializedName("stop_headsign") val stopHeadsign: String?,
    @SerializedName("trip_headsign") val tripHeadsign: String?,
    @SerializedName("trip_id") val tripId: String
)

data class TripUpdatesResponse(val response: TripUpdatesBody?)

data class TripUpdatesBody(val entity: List<TripUpdateEntity>?)

data class TripUpdateEntity(
    @SerializedName("trip_update") val tripUpdate: TripUpdate?
)

data class TripUpdate(
    val trip: TripInfo,
    val delay: Int?,
    @SerializedName("stop_time_update") val stopTimeUpdate: StopTimeUpdate?
)

data class TripInfo(
    @SerializedName("trip_id") val tripId: String,
    @SerializedName("schedule_relationship") val scheduleRelationship: Int?
)

data class StopTimeUpdate(val departure: StopTimeEvent?)

data class StopTimeEvent(val delay: Int?)
