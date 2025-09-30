package com.example.todoapp.data

import com.example.todoapp.network.RetrofitClient //Handles HTTP request
import com.example.todoapp.network.WeatherResponse

//Communicates with Weather API Service via Retrofit, hiding implementation.
//API comes from MainActivity
class WeatherRepository(private val apiKey: String) {
    //Used with Kotlin coroutines to fetch weather data for given location.
    suspend fun getWeather(location: String): WeatherResponse {
        return RetrofitClient.apiService.getWeatherForecast(
            apiKey = apiKey,
            location = location
        )
    }
}