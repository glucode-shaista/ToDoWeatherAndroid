package com.example.todoapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Sets up Retrofit Networking client
//Fetch weather data from WeatherAPI
//Singleton object
object RetrofitClient {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //Converts JSON responses
            .build()
            .create(WeatherApiService::class.java)
    }
}