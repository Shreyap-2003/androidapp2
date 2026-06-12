package com.example.composecustomerapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
) {
    val canSignIn: Boolean get() = phoneNumber.length == 10 && password.isNotEmpty()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(phoneNumber: String) {
        if (phoneNumber.length <= 10 && phoneNumber.all { it.isDigit() }) {
            _uiState.update { it.copy(phoneNumber = phoneNumber, error = null) }
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signIn(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (!currentState.canSignIn) {
            println("AuthDebug: UI Validation failed - Phone: ${currentState.phoneNumber.length}")
            return
        }

        viewModelScope.launch {
            println("AuthDebug: signIn() launching coroutine")
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.login(currentState.phoneNumber, currentState.password)
            
            result.onSuccess {
                println("AuthDebug: signIn() success")
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                onSuccess()
            }.onFailure { exception ->
                println("AuthDebug: signIn() failure: ${exception.message}")
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                LoginViewModel(application.authRepository)
            }
        }
    }
}
