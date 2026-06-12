package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OrdersUiState(
    val activeOrders: List<Order> = emptyList(),
    val completedOrders: List<Order> = emptyList(),
    val selectedTab: OrderTab = OrderTab.ACTIVE,
    val isLoading: Boolean = false
)

enum class OrderTab {
    ACTIVE, COMPLETED
}

class OrdersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        _uiState.update { 
            it.copy(
                activeOrders = listOf(
                    Order(
                        "65",
                        "65",
                        "Hide & Seek Chocochip Cookies",
                        30,
                        "IN_PROGRESS",
                        "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&q=80&w=200",
                        "June 8, 2026"
                    ),
                    Order(
                        "66",
                        "66",
                        "Unibic Fruit & Nut Cookies",
                        70,
                        "IN_PROGRESS",
                        "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&q=80&w=200",
                        "June 8, 2026"
                    )
                ),
                completedOrders = listOf(
                    Order(
                        "56",
                        "56",
                        "Milk",
                        27,
                        "COMPLETED",
                        "https://images.unsplash.com/photo-1550583724-125581fe2f8a?auto=format&fit=crop&q=80&w=200",
                        "29 May 2026"
                    ),
                    Order(
                        "57",
                        "57",
                        "Milk",
                        27,
                        "COMPLETED",
                        "https://images.unsplash.com/photo-1550583724-125581fe2f8a?auto=format&fit=crop&q=80&w=200",
                        "29 May 2026"
                    ),
                    Order(
                        "58",
                        "58",
                        "Coca-Cola",
                        39,
                        "COMPLETED",
                        "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&q=80&w=200",
                        "29 May 2026"
                    )
                )
            )
        }
    }

    fun setSelectedTab(tab: OrderTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
