package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.composecustomerapp.ui.components.Product

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val recentSearches: List<String> = listOf("Milk", "Cookies", "Coca-Cola", "Tomato"),
    val isLoading: Boolean = false
)

class SearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String, allProducts: List<Product>) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.length >= 2) {
            val results = allProducts.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.tag?.contains(query, ignoreCase = true) == true 
            }
            _uiState.update { it.copy(searchResults = results) }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }
}
