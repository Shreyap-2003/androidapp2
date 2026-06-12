package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.composecustomerapp.ui.components.Product

data class OrderDetailUiState(
    val order: Order? = null,
    val suggestedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

class OrderDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String, allOrders: List<Order>, allProducts: List<Product>) {
        val order = allOrders.find { it.id == orderId }
        val suggested = allProducts.take(3) // Mock suggested products
        
        _uiState.update { 
            it.copy(order = order, suggestedProducts = suggested)
        }
    }
}
