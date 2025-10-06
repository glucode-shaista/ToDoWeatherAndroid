package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.navigation.AppNavGraph
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.WeatherViewModel
//import com.example.todoapp.viewmodel.TaskViewModelFactory
//import com.example.todoapp.viewmodel.WeatherViewModelFactory
//import com.example.todoapp.data.TaskDatabase
//import com.example.todoapp.data.TaskRepository
//import com.example.todoapp.data.WeatherRepository
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.todoapp.data.OnboardingManagerInterface
import android.Manifest


import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    private val weatherViewModel : WeatherViewModel by viewModels()

    @Inject lateinit var onboardingManager: OnboardingManagerInterface

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result handled, now load weather (ViewModel will handle location internally)
            weatherViewModel.loadWeatherForCurrentLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DEMO MODE: Reset onboarding for showcasing - remove after demo
        //onboardingManager.resetOnboarding()

        //UI Setup, Theme and NavController
        setContent {
            ToDoAppTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    taskViewModel = taskViewModel,
                    weatherViewModel = weatherViewModel,
                    onboardingManager = onboardingManager,
                    onRequestLocationPermission = {
                        // Request location permission during onboarding
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                )
            }
        }
    }
}
