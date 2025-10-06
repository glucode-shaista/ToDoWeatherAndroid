package com.example.todoapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    //Tells retrofit to send GET requests to endpoint
    @GET("forecast.json")
    //Runs Asynchronously
    suspend fun getWeatherForecast(
        @Query("key") apiKey: String, //Appends to URL
        @Query("q") location: String,
        @Query("days") days: Int = 1 //Forecast number of days
    ): WeatherResponse //API responses parsed into WeatherResponse data class.
}