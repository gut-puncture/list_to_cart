package com.example.groceryapp.data.repository

import com.example.groceryapp.data.model.GroceryItem
import com.example.groceryapp.data.model.ProductRecommendation
import okhttp3.MultipartBody

interface GroceryRepository {
    suspend fun processImage(image: MultipartBody.Part): Result<List<GroceryItem>>
    suspend fun getRecommendations(itemName: String): Result<List<ProductRecommendation>>
}