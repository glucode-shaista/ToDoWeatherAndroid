package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.location.LocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides @Singleton
    fun provideLocationService(@ApplicationContext context: Context): LocationService =
        LocationService(context)
}


