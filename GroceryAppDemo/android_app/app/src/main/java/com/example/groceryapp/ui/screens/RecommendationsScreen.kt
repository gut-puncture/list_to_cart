package com.example.groceryapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groceryapp.data.model.GroceryItem
import com.example.groceryapp.data.model.ProductRecommendation
import com.example.groceryapp.data.model.SkuDetail
import com.example.groceryapp.ui.UiState
import com.example.groceryapp.ui.components.ItemWithRecommendations
import com.example.groceryapp.ui.components.LoadingComponent
import com.example.groceryapp.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    onBackPressed: () -> Unit,
    onCartClicked: () -> Unit,
    viewModel: GroceryViewModel
) {
    val groceryListState by viewModel.groceryList.collectAsState()
    val recommendationsState by viewModel.recommendations.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 8.dp),
                title = { Text("Your Grocery List") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp)  // Give more space around the entire element
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .offset(x = (-18).dp, y = 16.dp)
                                            .size(20.dp)  // Ensure consistent badge size
                                    ) {
                                        Text(
                                            text = cartItemCount.toString(),
                                            modifier = Modifier.padding(all = 2.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = onCartClicked) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val uiState = viewModel.uiState.collectAsState().value) {
                is UiState.Loading -> {
                    LoadingComponent()
                }
                is UiState.Success<*> -> {
                    if (groceryListState.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No items in your grocery list")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(groceryListState) { item ->
                                ItemWithRecommendations(
                                    item = item,
                                    recommendations = recommendationsState[item.item_name] ?: emptyList(),
                                    onAddToCart = { product, sku ->
                                        viewModel.addToCart(product, sku)
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.message)
                    }
                }
            }
        }
    }
}
