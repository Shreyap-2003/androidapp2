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
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

data class PartnerOrdersUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class PartnerOrdersViewModel(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartnerOrdersUiState())
    val uiState: StateFlow<PartnerOrdersUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedOrdersPaged: Flow<PagingData<Order>> = tokenManager.userId
        .flatMapLatest { userIdStr ->
            val userId = userIdStr?.toIntOrNull()
            if (userId != null) {
                orderRepository.getOrdersPaged(partnerId = userId, status = "COMPLETED")
            } else {
                flowOf(PagingData.empty())
            }
        }.cachedIn(viewModelScope)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                PartnerOrdersViewModel(
                    application.orderRepository,
                    application.tokenManager
                )
            }
        }
    }
}
