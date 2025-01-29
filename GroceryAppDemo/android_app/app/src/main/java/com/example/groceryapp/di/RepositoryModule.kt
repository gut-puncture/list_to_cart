package com.example.groceryapp.di

import com.example.groceryapp.data.network.ApiService
import com.example.groceryapp.data.repository.GroceryRepository
import com.example.groceryapp.data.repository.GroceryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideGroceryRepository(
        apiService: ApiService
    ): GroceryRepository {
        return GroceryRepositoryImpl(apiService)
    }
}