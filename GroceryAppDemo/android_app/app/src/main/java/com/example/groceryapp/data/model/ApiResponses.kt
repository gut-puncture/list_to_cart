package com.example.groceryapp.data.model

data class ProcessImageResponse(
    val grocery_list: List<GroceryItem>  // Remove the wrapper, server returns direct list
)

data class GroceryListWrapper(
    val grocery_list: List<GroceryItem>
)
data class RecommendationsResponse(
    val recommendations: List<ProductRecommendation>
)