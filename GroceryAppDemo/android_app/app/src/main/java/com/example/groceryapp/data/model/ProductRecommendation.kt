package com.example.groceryapp.data.model

import com.google.gson.annotations.SerializedName

data class ProductRecommendation(
    val product_name: String = "",  
    val description: String = "",   
    val similarity_score: Double = 0.0,
    val skus: List<SkuDetail> = emptyList(),
    @SerializedName("image_url") val imageUrl: String? = null
)