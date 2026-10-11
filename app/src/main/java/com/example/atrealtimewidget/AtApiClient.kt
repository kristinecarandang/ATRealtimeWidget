package com.example.atrealtimewidget

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// A single, reusable Retrofit instance pointed at the AT API.
// Needs the Gson converter so Retrofit knows how to turn the JSON
// response into your StopResponse data class automatically.
object AtApiClient {
    private const val BASE_URL = "https://api.at.govt.nz/"

    val service: AtApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AtApiService::class.java)
    }
}
