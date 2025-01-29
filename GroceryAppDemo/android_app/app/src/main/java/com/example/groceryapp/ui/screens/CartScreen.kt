package com.example.groceryapp.ui.screens

import com.example.groceryapp.ui.components.QuantitySelector
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groceryapp.data.model.CartItem
import com.example.groceryapp.ui.viewmodel.GroceryViewModel
import com.example.groceryapp.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackPressed: () -> Unit,
    viewModel: GroceryViewModel = hiltViewModel()
) {
    val cartItemsState by viewModel.cartItems.collectAsState()
    val cartItems = when (cartItemsState) {
        is UiState.Success<List<CartItem>> -> (cartItemsState as UiState.Success<List<CartItem>>).data
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { /* Clear cart logic, if any */ }) {
                            Icon(Icons.Default.Delete, "Clear cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyCartContent(modifier = Modifier.padding(padding))
        } else {
            CartContent(
                cartItems = cartItems,
                onUpdateQuantity = viewModel::updateQuantity,
                onRemoveItem = viewModel::removeFromCart,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun EmptyCartContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun CartContent(
    cartItems: List<CartItem>,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cartItems) { item ->
            CartItemCard(
                cartItem = item,
                onUpdateQuantity = { quantity -> onUpdateQuantity(item, quantity) },
                onRemoveItem = { onRemoveItem(item) }
            )
        }

        item {
            CartSummary(cartItems = cartItems)
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = cartItem.product.product_name,  // Changed from groceryItem.item_name
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cartItem.sku.quantity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemoveItem) {
                    Icon(Icons.Default.Close, "Remove item")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantitySelector(
                    quantity = cartItem.quantity,
                    onQuantityChange = onUpdateQuantity
                )
            }
        }
    }
}

@Composable
private fun CartSummary(cartItems: List<CartItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Items:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = cartItems.sumOf { it.quantity }.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { /* Handle checkout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}
