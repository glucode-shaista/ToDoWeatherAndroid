package com.example.todoapp.location

interface LocationServiceInterface {
    /**
     * Gets the current location of the device
     * @return Coordinates object with latitude and longitude, or null if location cannot be obtained
     */
    suspend fun getCurrentLocation(): Coordinates?
    
    /**
     * Checks if location permissions are granted
     * @return true if location permissions are available, false otherwise
     */
    fun hasLocationPermission(): Boolean
    
    /**
     * Gets the location name (city) from coordinates
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return The city name or null if geocoding fails
     */
    suspend fun getLocationName(latitude: Double, longitude: Double): String?
}
