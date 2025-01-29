package com.example.groceryapp.data.model

data class SkuDetail(
    val is_default: Boolean = false,
    val numeric_quantity: Double = 0.0,
    val quantity: String = "",     // Default empty string instead of null
    val unit: String = ""         // Default empty string instead of null
)