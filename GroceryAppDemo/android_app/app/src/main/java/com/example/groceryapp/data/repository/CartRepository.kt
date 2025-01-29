package com.example.groceryapp.data.repository

import com.example.groceryapp.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.add(item)
        _cartItems.value = currentItems
    }

    fun removeFromCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.remove(item)
        _cartItems.value = currentItems
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            currentItems[index] = currentItems[index].copy(quantity = quantity)
            _cartItems.value = currentItems
        }
    }
}