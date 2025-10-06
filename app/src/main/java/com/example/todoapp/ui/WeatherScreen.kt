package com.example.todoapp.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage
import com.example.todoapp.viewmodel.WeatherViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    onRequestLocationPermission: (() -> Unit)? = null) {
    //location: String = "Durban"
    val weather by weatherViewModel.weatherData.collectAsState()
    val isRefreshing by weatherViewModel.isRefreshing.collectAsState()
    val lastUpdated by weatherViewModel.lastUpdatedEpochSeconds.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { weatherViewModel.refresh() }
    )

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        weather?.let { weatherData ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                        .offset(y = (-4).dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Weather",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { weatherViewModel.refresh() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (isRefreshing) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        modifier = Modifier
                            .offset(y = (-6).dp), // move everything below heading up slightly
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Location name
                        Text(
                            text = "${weatherData.location.name}, ${weatherData.location.country}",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = "https:${weatherData.current.condition.icon}",
                                contentDescription = weatherData.current.condition.text,
                                modifier = Modifier.size(48.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${weatherData.current.temp_c}°C",
                                    //style = MaterialTheme.typography.headlineSmall,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = weatherData.current.condition.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp)) // small space to move it down

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center, // center the row
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌄 Sunrise: ${weatherData.forecast.forecastday.first().astro.sunrise}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(24.dp)) // spacing between sunrise and sunset
                            Text(
                                text = "🌅 Sunset: ${weatherData.forecast.forecastday.first().astro.sunset}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        lastUpdated?.let { epoch ->
                            val formatted = java.time.Instant.ofEpochSecond(epoch)
                                .atZone(java.time.ZoneId.systemDefault())
                                .format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Last updated: $formatted",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } ?: run {
                // Show fallback UI when no weather data (likely no location permission)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Weather",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Weather information unavailable",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Enable location access to get weather updates for your area",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        onRequestLocationPermission?.let { requestPermission ->
                            Button(
                                onClick = requestPermission,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enable Location")
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }
    }
}


//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun WeatherScreen(
//    weatherViewModel: WeatherViewModel,
//    onRequestLocationPermission: (() -> Unit)? = null
//) {
//    val weather by weatherViewModel.weatherData.collectAsState()
//    val isRefreshing by weatherViewModel.isRefreshing.collectAsState()
//    val lastUpdated by weatherViewModel.lastUpdatedEpochSeconds.collectAsState()
//
//    val pullRefreshState = rememberPullRefreshState(
//        refreshing = isRefreshing,
//        onRefresh = { weatherViewModel.refresh() }
//    )
//
//    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
//        weather?.let { weatherData ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                ),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Header: Weather title + refresh button
//                    Box(modifier = Modifier.fillMaxWidth()) {
//                        Text(
//                            text = "Weather",
//                            style = MaterialTheme.typography.titleLarge.copy(
//                                color = MaterialTheme.colorScheme.primary,
//                                fontWeight = FontWeight.Bold
//                            ),
//                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                        IconButton(
//                            onClick = { weatherViewModel.refresh() },
//                            modifier = Modifier.align(Alignment.CenterEnd)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Refresh,
//                                contentDescription = "Refresh",
//                                tint = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//                    }
//
//                    if (isRefreshing) {
//                        LinearProgressIndicator(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 4.dp),
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//
//                    // Location
//                    Text(
//                        text = "${weatherData.location.name}, ${weatherData.location.country}",
//                        modifier = Modifier.fillMaxWidth(),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Weather icon + temperature + condition
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        AsyncImage(
//                            model = "https:${weatherData.current.condition.icon}",
//                            contentDescription = weatherData.current.condition.text,
//                            modifier = Modifier.size(48.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(
//                                text = "${weatherData.current.temp_c}°C",
//                                style = MaterialTheme.typography.headlineSmall,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                            Text(
//                                text = weatherData.current.condition.text,
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Sunrise and Sunset - centered
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "🌄 Sunrise: ${weatherData.forecast.forecastday.first().astro.sunrise}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Spacer(modifier = Modifier.width(24.dp))
//                        Text(
//                            text = "🌅 Sunset: ${weatherData.forecast.forecastday.first().astro.sunset}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Last updated
//                    lastUpdated?.let { epoch ->
//                        val formatted = Instant.ofEpochSecond(epoch)
//                            .atZone(ZoneId.systemDefault())
//                            .format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
//                        Text(
//                            text = "Last updated: $formatted",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//            }
//        } ?: run {
//            // Fallback UI when no weather data
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                ),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Weather",
//                        style = MaterialTheme.typography.titleLarge.copy(
//                            color = MaterialTheme.colorScheme.primary,
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                    Icon(
//                        imageVector = Icons.Filled.LocationOn,
//                        contentDescription = "Location",
//                        modifier = Modifier.size(48.dp),
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Text(
//                        text = "Weather information unavailable",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Text(
//                        text = "Enable location access to get weather updates for your area",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
//                    )
//                    onRequestLocationPermission?.let { requestPermission ->
//                        Button(
//                            onClick = requestPermission,
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.primary
//                            )
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.LocationOn,
//                                contentDescription = null,
//                                modifier = Modifier.size(16.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Enable Location")
//                        }
//                    }
//                }
//            }
//        }
//
//        PullRefreshIndicator(
//            refreshing = isRefreshing,
//            state = pullRefreshState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 8.dp)
//        )
//    }
//}
