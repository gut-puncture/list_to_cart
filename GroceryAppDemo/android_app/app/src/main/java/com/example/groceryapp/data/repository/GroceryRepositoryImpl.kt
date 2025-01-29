// GroceryRepositoryImpl.kt
package com.example.groceryapp.data.repository

import android.util.Log
import com.example.groceryapp.data.model.GroceryItem
import com.example.groceryapp.data.model.ProductRecommendation
import com.example.groceryapp.data.network.ApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GroceryRepository {
    
    override suspend fun processImage(image: MultipartBody.Part): Result<List<GroceryItem>> {
        return try {
            val response = apiService.processImage(image)
            Result.success(response.grocery_list)
        } catch (e: Exception) {
            Log.e("GroceryRepository", "Error processing image", e)
            Result.failure(e)
        }
    }

    override suspend fun getRecommendations(itemName: String): Result<List<ProductRecommendation>> {
        return try {
            val response = apiService.getRecommendations(mapOf("item_name" to itemName))
            Log.d("GroceryRepository", "Raw API Response: $response")
            Result.success(response.recommendations)
        } catch (e: Exception) {
            Log.e("GroceryRepository", "Error getting recommendations", e)
            Result.failure(e)
        }
    }
}