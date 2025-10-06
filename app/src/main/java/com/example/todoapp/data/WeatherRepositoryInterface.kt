package com.example.todoapp.data

import com.example.todoapp.network.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepositoryInterface {
    // Get weather data (cache-first approach)
    suspend fun getWeather(location: String): WeatherResponse
    
    // Get weather as Flow for reactive UI updates
    fun getWeatherFlow(location: String): Flow<WeatherResponse?>
    
    // Force refresh weather data from API
    suspend fun refreshWeather(location: String): WeatherResponse
    
    // Clear cached weather data
    suspend fun clearCache()
    
    // Clear cache for specific location
    suspend fun clearCacheForLocation(location: String)
}
