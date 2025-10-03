package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun getWeatherByLocation(locationKey: String): WeatherCacheEntity?

    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey")
    fun getWeatherByLocationFlow(locationKey: String): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun deleteWeatherByLocation(locationKey: String)

    @Query("DELETE FROM weather_cache")
    suspend fun clearAllWeather()
}
