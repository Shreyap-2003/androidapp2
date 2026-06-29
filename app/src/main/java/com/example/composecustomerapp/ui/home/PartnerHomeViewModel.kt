package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.data.repository.AuthRepository
import com.example.composecustomerapp.data.repository.OrderRepository
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PartnerHomeUiState(
    val username: String = "",
    val acceptedOrders: List<Order> = emptyList(), // Orders this partner is currently handling
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class PartnerHomeViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartnerHomeUiState())
    val uiState: StateFlow<PartnerHomeUiState> = _uiState.asStateFlow()

    val openOrdersPaged: Flow<PagingData<Order>> = orderRepository.getOrdersPaged(status = "OPEN")
        .cachedIn(viewModelScope)

    init {
        loadPartnerData()
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(10000) // Poll every 10 seconds
                loadDataInternal()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            loadDataInternal()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadPartnerData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loadDataInternal()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadDataInternal() {
        val userIdStr = tokenManager.userId.first()
        val userIdInt = userIdStr?.toIntOrNull()
        
        if (userIdStr != null) {
            authRepository.getUserProfile(userIdStr).onSuccess { user ->
                _uiState.update { it.copy(username = user.firstName ?: "Partner") }
            }
        }

        // Fetch accepted orders for this partner
        val activeOrdersResult = if (userIdInt != null) {
            orderRepository.getPartnerActiveOrders(userIdInt)
        } else {
            Result.success(emptyList())
        }

        activeOrdersResult.onSuccess { active ->
            _uiState.update { 
                it.copy(
                    acceptedOrders = active
                )
            }
        }.onFailure { e ->
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun acceptOrder(orderId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val userIdStr = tokenManager.userId.first()
                val userId = userIdStr?.toIntOrNull()
                
                if (userId == null) {
                    onResult(false, "User session expired. Please login again.")
                    return@launch
                }

                val idToAccept = orderId.toIntOrNull()
                if (idToAccept == null) {
                    onResult(false, "Invalid Order ID: $orderId")
                    return@launch
                }

                orderRepository.assignPartner(idToAccept, userId).onSuccess {
                    loadPartnerData() // Refresh list
                    onResult(true, null)
                }.onFailure { e ->
                    onResult(false, e.message ?: "Failed to accept order")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun completeOrder(orderId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val userIdStr = tokenManager.userId.first()
                val userId = userIdStr?.toIntOrNull()

                if (userId == null) {
                    onResult(false, "User session expired. Please login again.")
                    return@launch
                }

                val idToComplete = orderId.toIntOrNull()
                if (idToComplete == null) {
                    onResult(false, "Invalid Order ID: $orderId")
                    return@launch
                }

                orderRepository.completeOrder(idToComplete, userId).onSuccess {
                    loadPartnerData()
                    onResult(true, null)
                }.onFailure { e ->
                    onResult(false, e.message ?: "Failed to complete order")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "An unexpected error occurred")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                PartnerHomeViewModel(
                    application.orderRepository,
                    application.authRepository,
                    application.tokenManager
                )
            }
        }
    }
}
