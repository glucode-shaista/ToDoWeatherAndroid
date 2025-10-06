package com.example.todoapp.data

import com.example.todoapp.network.WeatherResponse
import java.time.LocalDateTime

// DTO -> Entity (for caching)
fun WeatherResponse.toEntity(locationKey: String): WeatherCacheEntity {
    return WeatherCacheEntity(
        locationKey = locationKey,
        locationName = location.name,
        region = location.region,
        country = location.country,
        localTime = location.localtime,
        temperatureCelsius = current.temp_c,
        conditionText = current.condition.text,
        conditionIcon = current.condition.icon,
        sunrise = forecast.forecastday.firstOrNull()?.astro?.sunrise ?: "",
        sunset = forecast.forecastday.firstOrNull()?.astro?.sunset ?: "",
        lastUpdated = LocalDateTime.now()
    )
}

// Entity -> DTO (from cache) - reconstructs WeatherResponse for UI compatibility
fun WeatherCacheEntity.toWeatherResponse(): WeatherResponse {
    return WeatherResponse(
        location = com.example.todoapp.network.Location(
            name = locationName,
            region = region,
            country = country,
            localtime = localTime
        ),
        current = com.example.todoapp.network.Current(
            temp_c = temperatureCelsius,
            condition = com.example.todoapp.network.WeatherCondition(
                text = conditionText,
                icon = conditionIcon
            )
        ),
        forecast = com.example.todoapp.network.Forecast(
            forecastday = listOf(
                com.example.todoapp.network.ForecastDay(
                    astro = com.example.todoapp.network.Astro(
                        sunrise = sunrise,
                        sunset = sunset
                    )
                )
            )
        )
    )
}
