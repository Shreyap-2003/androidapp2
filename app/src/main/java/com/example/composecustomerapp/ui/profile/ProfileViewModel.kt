package com.example.composecustomerapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val role: String = "CUSTOMER",
    val location: String = "",
    val coordinates: String = "",
    val membershipType: String = "Premium Member",
    val membershipSince: String = "Member since Feb 2024",
    val status: String = "ACTIVE ACCOUNT",
    val profileImageUrl: String = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = tokenManager.userId.first()
            if (userId != null) {
                val result = repository.getUserProfile(userId)
                result.onSuccess { user ->
                    _uiState.update { 
                        it.copy(
                            firstName = user.firstName ?: "",
                            lastName = user.lastName ?: "",
                            fullName = "${user.firstName} ${user.lastName}",
                            phoneNumber = user.phoneNumber ?: "",
                            role = user.userType ?: "CUSTOMER",
                            location = user.address ?: "",
                            coordinates = if (user.latitude != null && user.longitude != null) 
                                "${user.latitude}, ${user.longitude}" else "",
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLogoutComplete()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                ProfileViewModel(application.authRepository, application.tokenManager)
            }
        }
    }
}
