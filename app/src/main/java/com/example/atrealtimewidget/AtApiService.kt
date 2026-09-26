package com.example.atrealtimewidget
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AtApiService {
    @GET("gtfs/v3/stops/{stopId}")
    suspend fun getStop(
        @Path("stopId") stopId: String,
        @Header("Ocp-Apim-Subscription-Key") apiKey: String
    ): StopResponse
}

typealias StopResponse = Map<String, Any>