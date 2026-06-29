package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.data.repository.OrderRepository
import com.example.composecustomerapp.ui.components.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderDetailUiState(
    val order: Order? = null,
    val suggestedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrderDetailViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String, allProducts: List<Product>) {
        val idInt = orderId.toIntOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = orderRepository.getOrderById(idInt)
            result.onSuccess { order ->
                val suggested = allProducts.take(3)
                _uiState.update { 
                    it.copy(order = order, suggestedProducts = suggested, isLoading = false)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                OrderDetailViewModel(application.orderRepository)
            }
        }
    }
}
