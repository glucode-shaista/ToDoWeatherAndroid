package com.example.todoapp.di

import com.example.todoapp.BuildConfig
import com.example.todoapp.data.WeatherDao
import com.example.todoapp.data.WeatherRepository
import com.example.todoapp.network.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        weatherDao: WeatherDao
    ): WeatherRepository {
        // Provide a default API key if not set to prevent crashes
        val apiKey = if (BuildConfig.WEATHER_API_KEY.isNotBlank()) {
            BuildConfig.WEATHER_API_KEY
        } else {
            "demo_key" // This will cause API calls to fail gracefully, but won't crash the app
        }
        
        return WeatherRepository(
            apiService = apiService,
            weatherDao = weatherDao,
            apiKey = apiKey
        )
    }
}