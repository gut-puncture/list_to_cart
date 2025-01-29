package com.example.groceryapp.data.model

data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val product: ProductRecommendation,
    val sku: SkuDetail,
    val quantity: Int
) {
    constructor(product: ProductRecommendation, sku: SkuDetail, quantity: Int) : this(
        id = java.util.UUID.randomUUID().toString(),
        product = product,
        sku = sku,
        quantity = quantity
    )
}