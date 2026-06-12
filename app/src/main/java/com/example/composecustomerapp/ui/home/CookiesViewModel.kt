package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.composecustomerapp.ui.components.Product

data class CookiesUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

class CookiesViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CookiesUiState())
    val uiState: StateFlow<CookiesUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = itemRepository.getItems()
            result.onSuccess { itemResponses ->
                val cookiesProducts = itemResponses
                    .filter { it.subCategoryId == 13 }
                    .map {
                        Product(
                            id = it.id.toString(),
                            name = it.name,
                            price = it.price.toInt(),
                            imageUrl = it.imageUrl
                        )
                    }
                _uiState.update { it.copy(products = cookiesProducts, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                CookiesViewModel(application.itemRepository)
            }
        }
    }
}
