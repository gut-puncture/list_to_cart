package com.example.groceryapp.ui.viewmodel 


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.model.*
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.GroceryRepository
import com.example.groceryapp.ui.UiState
import com.example.groceryapp.ui.viewmodel.GroceryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    // Initialize all MutableStateFlow and SharedFlow objects
    private val _groceryList = MutableStateFlow<List<GroceryItem>>(emptyList())
    val groceryList: StateFlow<List<GroceryItem>> = _groceryList.asStateFlow()

    private val _recommendations = MutableStateFlow<Map<String, List<ProductRecommendation>>>(emptyMap())
    val recommendations: StateFlow<Map<String, List<ProductRecommendation>>> = _recommendations.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CartItem>>> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    val cartItems = cartRepository.cartItems
        .map { items -> UiState.Success(items) }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            UiState.Loading
        )

    val cartItemCount = cartRepository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            0
        )

    fun processImage(file: File) {
        viewModelScope.launch {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                
                groceryRepository.processImage(body).fold(
                    onSuccess = { items ->
                        _groceryList.emit(items)
                        _uiState.emit(UiState.Success(emptyList()))
                        items.forEach { item ->
                            fetchRecommendations(item.item_name)
                        }
                    },
                    onFailure = { e ->
                        _errorEvents.emit("Failed to process image: ${e.message}")
                        _uiState.emit(UiState.Error("Failed to process image"))
                    }
                )
            } catch (e: Exception) {
                _errorEvents.emit("Failed to process image: ${e.message}")
                _uiState.emit(UiState.Error("Failed to process image"))
            }
        }
    }

    fun updateGroceryList(items: List<GroceryItem>) {
        viewModelScope.launch {
            _groceryList.emit(items)
            items.forEach { item ->
                fetchRecommendations(item.item_name)
            }
        }
    }

    private fun fetchRecommendations(itemName: String) {
        println("DEBUG: Fetching recommendations for $itemName")
        viewModelScope.launch {
            try {
                groceryRepository.getRecommendations(itemName).fold(
                    onSuccess = { recommendations ->
                        val currentRecommendations = _recommendations.value.toMutableMap()
                        currentRecommendations[itemName] = recommendations
                        _recommendations.emit(currentRecommendations)
                    },
                    onFailure = { e ->
                        _uiState.emit(UiState.Error(e.message ?: "Failed to fetch recommendations"))
                    }
                )
            } catch (e: Exception) {
                _uiState.emit(UiState.Error(e.message ?: "Failed to fetch recommendations"))
            }
        }
    }

    fun addToCart(product: ProductRecommendation, sku: SkuDetail, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(CartItem(product = product, sku = sku, quantity = quantity))
                _errorEvents.emit("Added to cart")
            } catch (e: Exception) {
                _errorEvents.emit("Failed to add to cart")
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(item)
                _errorEvents.emit("Removed from cart")
            } catch (e: Exception) {
                _errorEvents.emit("Failed to remove from cart")
            }
        }
    }

    fun updateQuantity(item: CartItem, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.updateQuantity(item.id, quantity)
                _errorEvents.emit("Quantity updated")
            } catch (e: Exception) {
                _errorEvents.emit("Failed to update quantity")
            }
        }
    }
}