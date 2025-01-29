package com.example.groceryapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.groceryapp.data.model.ProductRecommendation
import com.example.groceryapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: ProductRecommendation,
    onAddToCart: () -> Unit,
    onUpdateQuantity: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Log.d("ProductCard", "Product: $product")

    val defaultSku = remember(product) {
        product.skus.firstOrNull { it.is_default } ?: product.skus.firstOrNull()
    }
    var isAddedToCart by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(0) }

    Card(
        modifier = modifier
            .width(180.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl.also { Log.d("ProductCard", "Loading image URL: $it") },
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.placeholder_product),
                    onLoading = { Log.d("ProductCard", "Loading image...") },
                    onError = { Log.e("ProductCard", "Error loading image: ${it.result.throwable}") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            if (product.product_name.isNotEmpty()) {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Description
            if (product.description.isNotEmpty()) {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Default SKU quantity
            defaultSku?.let { sku ->
                if (sku.quantity.isNotEmpty()) {
                    Text(
                        text = sku.quantity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Match Score
            Text(
                text = "Match: ${String.format("%.1f", product.similarity_score)}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            AddToCartButton(
                isAdded = isAddedToCart,
                quantity = quantity,
                onAddToCart = {
                    isAddedToCart = true
                    quantity = 1
                    defaultSku?.let { sku ->
                        onAddToCart()
                        onUpdateQuantity(1)
                    }
                },
                onIncrement = {
                    quantity++
                    defaultSku?.let { sku ->
                        onUpdateQuantity(quantity)
                    }
                },
                onDecrement = {
                    if (quantity > 0) {
                        quantity--
                        defaultSku?.let { sku ->
                            onUpdateQuantity(quantity)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}