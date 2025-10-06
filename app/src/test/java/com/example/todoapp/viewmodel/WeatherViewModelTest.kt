package com.example.todoapp.viewmodel

import com.example.todoapp.data.WeatherRepository
import com.example.todoapp.location.Coordinates
import com.example.todoapp.location.LocationServiceInterface
import com.example.todoapp.network.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockLocationService: LocationServiceInterface
    private lateinit var viewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk<WeatherRepository>(relaxed = true)
        mockLocationService = mockk(relaxed = true)
        viewModel = WeatherViewModel(mockRepository, mockLocationService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `viewModel can be created successfully`() = runTest {
        // Given: Mock dependencies
        
        // When: No action needed (ViewModel is created)
        
        // Then: ViewModel should be created
        assertNotNull(viewModel)
        assertNotNull(viewModel.weatherData)
        assertNotNull(viewModel.isRefreshing)
        assertNotNull(viewModel.lastUpdatedEpochSeconds)
    }

    @Test
    fun `loadWeather delegates to repository correctly`() = runTest {
        // Given
        val testLocation = "London,UK"
        val mockWeatherResponse = createMockWeatherResponse("London")

        coEvery { mockRepository.getWeather(testLocation) } returns mockWeatherResponse

        // When
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.weatherData.value)
        assertEquals(mockWeatherResponse, viewModel.weatherData.value)
        assertFalse(viewModel.isRefreshing.value)
        assertNotNull(viewModel.lastUpdatedEpochSeconds.value)
        
        coVerify { mockRepository.getWeather(testLocation) }
    }

    @Test
    fun `loadWeather handles repository exceptions gracefully`() = runTest {
        // Given
        val testLocation = "InvalidLocation"
        val exception = Exception("API Error")
        
        coEvery { mockRepository.getWeather(testLocation) } throws exception

        // When
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.weatherData.value)
        assertFalse(viewModel.isRefreshing.value)
        
        coVerify { mockRepository.getWeather(testLocation) }
    }

    @Test
    fun `refresh delegates to repository refresh method`() = runTest {
        // Given
        val testLocation = "New York"
        val mockWeatherResponse = createMockWeatherResponse("New York")
        
        // First load weather to set lastQuery
        coEvery { mockRepository.getWeather(testLocation) } returns mockWeatherResponse
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Setup refresh mock
        coEvery { mockRepository.refreshWeather(testLocation) } returns mockWeatherResponse.copy(
            current = mockWeatherResponse.current.copy(temp_c = 25.0f)
        )

        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.weatherData.value)
        assertEquals(25.0f, viewModel.weatherData.value?.current?.temp_c)
        assertFalse(viewModel.isRefreshing.value)
        
        coVerify { mockRepository.refreshWeather(testLocation) }
    }

    @Test
    fun `refresh does nothing when no lastQuery exists`() = runTest {
        // Given: ViewModel with no previous weather loaded
        
        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.weatherData.value)
        coVerify(exactly = 0) { mockRepository.refreshWeather(any()) }
    }

    @Test
    fun `refresh handles repository exceptions gracefully`() = runTest {
        // Given
        val testLocation = "London"
        val mockWeatherResponse = createMockWeatherResponse("London")
        
        // Setup initial load
        coEvery { mockRepository.getWeather(testLocation) } returns mockWeatherResponse
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Setup refresh to throw exception
        coEvery { mockRepository.refreshWeather(testLocation) } throws Exception("Network Error")

        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.isRefreshing.value)
        coVerify { mockRepository.refreshWeather(testLocation) }
    }

    @Test
    fun `loadWeatherForCurrentLocation checks permission first`() = runTest {
        // Given
        every { mockLocationService.hasLocationPermission() } returns false

        // When
        viewModel.loadWeatherForCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.weatherData.value)
        verify { mockLocationService.hasLocationPermission() }
        coVerify(exactly = 0) { mockLocationService.getCurrentLocation() }
        coVerify(exactly = 0) { mockRepository.getWeather(any()) }
    }

    @Test
    fun `loadWeatherForCurrentLocation uses coordinates when available`() = runTest {
        // Given
        val coordinates = Coordinates(40.7128, -74.0060)
        val mockWeatherResponse = createMockWeatherResponse("New York, NY")
        
        every { mockLocationService.hasLocationPermission() } returns true
        coEvery { mockLocationService.getCurrentLocation() } returns coordinates
        coEvery { mockRepository.getWeather("40.7128, -74.0060") } returns mockWeatherResponse

        // When
        viewModel.loadWeatherForCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.weatherData.value)
        
        verify { mockLocationService.hasLocationPermission() }
        coVerify { mockLocationService.getCurrentLocation() }
        coVerify { mockRepository.getWeather("40.7128, -74.006") }
    }

    @Test
    fun `loadWeatherForCurrentLocation falls back to default when coordinates null`() = runTest {
        // Given
        val mockWeatherResponse = createMockWeatherResponse("Johannesburg")
        
        every { mockLocationService.hasLocationPermission() } returns true
        coEvery { mockLocationService.getCurrentLocation() } returns null
        coEvery { mockRepository.getWeather("Johannesburg") } returns mockWeatherResponse

        // When
        viewModel.loadWeatherForCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.weatherData.value)
        assertEquals(mockWeatherResponse, viewModel.weatherData.value)
        
        verify { mockLocationService.hasLocationPermission() }
        coVerify { mockLocationService.getCurrentLocation() }
        coVerify { mockRepository.getWeather("Johannesburg") }
    }

    @Test
    fun `loadWeatherForCurrentLocation falls back when location service throws exception`() = runTest {
        // Given
        val exception = Exception("Location service error")
        val mockWeatherResponse = createMockWeatherResponse("Johannesburg")
        
        every { mockLocationService.hasLocationPermission() } returns true
        coEvery { mockLocationService.getCurrentLocation() } throws exception
        coEvery { mockRepository.getWeather("Johannesburg") } returns mockWeatherResponse

        // When
        viewModel.loadWeatherForCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.weatherData.value)
        assertEquals(mockWeatherResponse, viewModel.weatherData.value)
        
        verify { mockLocationService.hasLocationPermission() }
        coVerify { mockLocationService.getCurrentLocation() }
        coVerify { mockRepository.getWeather("Johannesburg") }
    }

    @Test
    fun `isRefreshing state is managed correctly during weather loading`() = runTest {
        // Given
        val testLocation = "Tokyo"
        val mockWeatherResponse = createMockWeatherResponse("Tokyo")
        
        coEvery { mockRepository.getWeather(testLocation) } returns mockWeatherResponse

        // When
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Should no longer be refreshing after completion
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `lastUpdatedEpochSeconds is updated on successful weather load`() = runTest {
        // Given
        val testLocation = "Paris"
        val mockWeatherResponse = createMockWeatherResponse("Paris")
        
        coEvery { mockRepository.getWeather(testLocation) } returns mockWeatherResponse
        
        val initialTimestamp = viewModel.lastUpdatedEpochSeconds.value

        // When
        viewModel.loadWeather(testLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val updatedTimestamp = viewModel.lastUpdatedEpochSeconds.value
        assertNotNull(updatedTimestamp)
        assertNotEquals(initialTimestamp, updatedTimestamp)
    }

    @Test
    fun `location coordinates are formatted correctly`() = runTest {
        // Given
        val coordinates = Coordinates(-26.2041, 28.0473) // Johannesburg coordinates
        val expectedFormat = "-26.2041, 28.0473"
        
        val mockWeatherResponse = createMockWeatherResponse("Johannesburg")
        
        every { mockLocationService.hasLocationPermission() } returns true
        coEvery { mockLocationService.getCurrentLocation() } returns coordinates
        coEvery { mockRepository.getWeather(expectedFormat) } returns mockWeatherResponse

        // When
        viewModel.loadWeatherForCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.getWeather(expectedFormat) }
    }

    @Test
    fun `multiple weather loads work independently`() = runTest {
        // Given
        val locations = listOf("Berlin", "Tokyo", "Sydney")
        val responses = locations.map { createMockWeatherResponse(it) }
        
        locations.zip(responses).forEach { (location, response) ->
            coEvery { mockRepository.getWeather(location) } returns response
        }

        // When: Load multiple locations
        locations.forEach { location ->
            viewModel.loadWeather(location)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Then: Should have the last loaded weather
        assertNotNull(viewModel.weatherData.value)
        assertEquals("Sydney", viewModel.weatherData.value?.location?.name)
        
        locations.forEach { location ->
            coVerify { mockRepository.getWeather(location) }
        }
    }

    // Helper function to create mock weather responses
    private fun createMockWeatherResponse(cityName: String) = WeatherResponse(
        location = Location(
            name = cityName,
            region = "Greater ${cityName}",
            country = "Test Country",
            localtime = "2023-10-03 12:00"
        ),
        current = Current(
            temp_c = 23.5f,
            condition = WeatherCondition(
                text = "Sunny",
                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
            )
        ),
        forecast = Forecast(
            forecastday = listOf(
                ForecastDay(
                    astro = Astro(
                        sunrise = "06:30 AM",
                        sunset = "18:45 PM"
                    )
                )
            )
        )
    )
}
