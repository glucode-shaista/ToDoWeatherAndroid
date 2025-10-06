package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.TaskDatabase
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.TaskRepositoryInterface
import com.example.todoapp.data.OnboardingManager
import com.example.todoapp.data.OnboardingManagerInterface
import com.example.todoapp.location.LocationService
import com.example.todoapp.location.LocationServiceInterface
import com.example.todoapp.data.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase =
        TaskDatabase.getDatabase(context)

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideWeatherDao(db: TaskDatabase): WeatherDao = db.weatherDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepositoryInterface = TaskRepository(taskDao)
    
    @Provides
    @Singleton
    fun provideOnboardingManager(@ApplicationContext context: Context): OnboardingManagerInterface = 
        OnboardingManager(context)
    
    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): LocationServiceInterface = 
        LocationService(context)
}