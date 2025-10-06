package com.example.todoapp.data

import com.example.todoapp.network.WeatherApiService
import com.example.todoapp.network.WeatherResponse
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val apiKey: String
) {
    companion object {
        private const val CACHE_DURATION_MINUTES = 15L
    }

    // Backward compatible method - returns WeatherResponse for existing UI
    suspend fun getWeather(location: String): WeatherResponse {
        val locationKey = location.trim()
        
        // Try cache first
        val cachedWeather = weatherDao.getWeatherByLocation(locationKey)
        if (cachedWeather != null && !isStale(cachedWeather.lastUpdated)) {
            println("Weather loaded from cache for $locationKey")
            return cachedWeather.toWeatherResponse()
        }

        // Fetch from network
        return try {
            println("Fetching weather from network for $locationKey")
            val response = apiService.getWeatherForecast(
                apiKey = apiKey,
                location = location
            )
            
            // Cache the response
            weatherDao.insertWeather(response.toEntity(locationKey))
            println("Weather cached for $locationKey")
            
            response
        } catch (e: Exception) {
            println("Network error: ${e.message}")
            // If network fails and we have stale cache, return it
            cachedWeather?.toWeatherResponse() ?: throw e
        }
    }

    // Force refresh from network
    suspend fun refreshWeather(location: String): WeatherResponse {
        val locationKey = location.trim()
        
        return try {
            val response = apiService.getWeatherForecast(
                apiKey = apiKey,
                location = location
            )
            
            weatherDao.insertWeather(response.toEntity(locationKey))
            println("Weather refreshed and cached for $locationKey")
            response
        } catch (e: Exception) {
            println("Failed to refresh weather: ${e.message}")
            throw e
        }
    }

    private fun isStale(lastUpdated: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        val minutesElapsed = ChronoUnit.MINUTES.between(lastUpdated, now)
        return minutesElapsed >= CACHE_DURATION_MINUTES
    }
}
