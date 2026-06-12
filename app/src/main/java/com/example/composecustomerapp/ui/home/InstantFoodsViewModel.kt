package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.composecustomerapp.ui.components.SubCategory

data class InstantFoodsUiState(
    val subCategories: List<SubCategory> = emptyList(),
    val isLoading: Boolean = false
)

class InstantFoodsViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(InstantFoodsUiState())
    val uiState: StateFlow<InstantFoodsUiState> = _uiState.asStateFlow()

    init {
        fetchSubCategories()
    }

    private fun fetchSubCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = categoryRepository.getSubCategories()
            result.onSuccess { subCategoryResponses ->
                val instantFoodsSubCategories = subCategoryResponses
                    .filter { it.categoryId == 18 }
                    .map {
                        val fullImageUrl = if (it.imageUrl.startsWith("http")) {
                            it.imageUrl
                        } else {
                            "http://10.200.24.230:8080/${it.imageUrl}"
                        }
                        SubCategory(
                            title = it.name,
                            description = it.description,
                            imageUrl = fullImageUrl
                        )
                    }
                _uiState.update { it.copy(subCategories = instantFoodsSubCategories, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                InstantFoodsViewModel(application.categoryRepository)
            }
        }
    }
}
