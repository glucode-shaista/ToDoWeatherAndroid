package com.example.todoapp.viewmodel

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.todoapp.data.WeatherRepository

//Create WeatherViewModel instance with WeatherRepo dependency
//class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    //override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            //@Suppress("UNCHECKED_CAST")
                //return WeatherViewModel(repository) as T
        //}
        //throw IllegalArgumentException("Unknown ViewModel Class")
    //}
//}

@Deprecated("Replaced by Hilt-injected ViewModel")
class WeatherViewModelFactory