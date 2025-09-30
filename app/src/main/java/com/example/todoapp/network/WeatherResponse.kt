package com.example.todoapp.network

//import android.location.Location

//Maps JSON response from weather API into kotlin data class using GSON via Retrofit
data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

data class Current(
    val temp_c: Float,
    val condition : WeatherCondition
)

data class WeatherCondition(
    val text: String,
    val icon: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val astro: Astro
)

data class Astro(
    val sunrise: String,
    val sunset: String
)