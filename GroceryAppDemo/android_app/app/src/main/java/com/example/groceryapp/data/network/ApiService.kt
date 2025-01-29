package com.example.groceryapp.data.network

import com.example.groceryapp.data.model.ProcessImageResponse
import com.example.groceryapp.data.model.RecommendationsResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("process_image")
    suspend fun processImage(
        @Part image: MultipartBody.Part
    ): ProcessImageResponse

    @Headers("Content-Type: application/json")
    @POST("recommendations")
    suspend fun getRecommendations(
        @Body request: Map<String, String>
    ): RecommendationsResponse
}