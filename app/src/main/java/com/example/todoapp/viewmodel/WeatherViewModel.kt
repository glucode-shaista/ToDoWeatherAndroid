package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.WeatherRepository
import com.example.todoapp.network.Location
import com.example.todoapp.network.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant //Pull to refresh
import kotlinx.coroutines.launch

//Uses repo to get weather and manage UI state
//Manages weather data loading Using Repo and exposes to UI via StateFlow
class WeatherViewModel(
    private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData
    //Pull to refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    private val _lastUpdatedEpochSeconds = MutableStateFlow<Long?>(null)
    val lastUpdatedEpochSeconds: StateFlow<Long?> = _lastUpdatedEpochSeconds
    private var lastQuery: String? = null

    fun loadWeather(location: String) {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true //Pull to refresh
                lastQuery = location
                val response = repository.getWeather(location)
                _weatherData.value = response
                _lastUpdatedEpochSeconds.value = Instant.now().epochSecond //Pull to refresh
                println("weather loaded successfully: $response")
            } catch (e: Exception) {
                e.printStackTrace()
                println("failed to load weather: ${e.message}")
                _weatherData.value = null
            } finally {
                _isRefreshing.value = false //Pull to refresh
            }
        }
    }

    //Pull to refresh
    fun refresh() {
        val query = lastQuery ?: return
        loadWeather(query)
    }
}