package com.example.atrealtimewidget
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

interface AtApiService {
    @GET("gtfs/v3/stops/{stopId}")
    suspend fun getStop(
        @Path("stopId") stopId: String,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): StopResponse
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

