package com.example.groceryapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.groceryapp.data.model.GroceryItem
import com.example.groceryapp.data.model.ProductRecommendation
import com.example.groceryapp.data.model.SkuDetail

@Composable
fun ItemWithRecommendations(
    item: GroceryItem,
    recommendations: List<ProductRecommendation>,
    onAddToCart: (ProductRecommendation, SkuDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        // Item Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.item_name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recommendations
        when {
            recommendations.isEmpty() -> {
                Text(
                    text = "Loading recommendations...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                Text(
                    text = "Recommended Products",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recommendations) { product ->
                        // Get the first SKU or the default one
                        val skuDetail = product.skus.firstOrNull { it.is_default } ?: product.skus.firstOrNull()
                        
                        // Only proceed if SKU is available
                        skuDetail?.let { detail ->
                            ProductCard(
                                product = product,
                                onAddToCart = { // Callback to add product to cart
                                    onAddToCart(product, detail)
                                }
                            )
                        }
                    }
                }
            }
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemWithRecommendations() {
    val sampleItem = GroceryItem(
        item_name = "Milk",
        quantity = 1.0,
        unit = "gallon"
    )

    val sampleRecommendations = listOf(
        ProductRecommendation(
            product_name = "Organic Milk",
            description = "Fresh organic milk",
            similarity_score = 95.0,
            skus = listOf(
                SkuDetail(
                    is_default = true,
                    numeric_quantity = 1.0,
                    quantity = "1 gallon",
                    unit = "gallon"
                )
            )
        )
    )

    ItemWithRecommendations(
        item = sampleItem,
        recommendations = sampleRecommendations,
        onAddToCart = { _, _ -> }
    )
}