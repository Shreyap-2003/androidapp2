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

data class SoupsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

class SoupsViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SoupsUiState())
    val uiState: StateFlow<SoupsUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = itemRepository.getItems()
            result.onSuccess { itemResponses ->
                val soupsProducts = itemResponses
                    .filter { it.subCategoryId == 17 }
                    .map {
                        Product(
                            id = it.id.toString(),
                            name = it.name,
                            price = it.price.toInt(),
                            imageUrl = it.imageUrl
                        )
                    }
                _uiState.update { it.copy(products = soupsProducts, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                SoupsViewModel(application.itemRepository)
            }
        }
    }
}
