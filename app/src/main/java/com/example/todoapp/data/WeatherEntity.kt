package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String, // "lat,lng" or city name
    val locationName: String,
    val region: String,
    val country: String,
    val localTime: String,
    val temperatureCelsius: Float,
    val conditionText: String,
    val conditionIcon: String,
    val sunrise: String,
    val sunset: String,
    val lastUpdated: LocalDateTime
)
