package com.example.composecustomerapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.model.RegisterRequest
import com.example.composecustomerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UserType {
    CUSTOMER, PARTNER
}

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val address: String = "",
    val isPasswordVisible: Boolean = false,
    val userType: UserType = UserType.CUSTOMER,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
) {
    val canRegister: Boolean get() = firstName.isNotEmpty() && 
            lastName.isNotEmpty() && 
            phoneNumber.length == 10 && 
            password.length >= 5 &&
            address.isNotEmpty()
}

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFirstNameChanged(name: String) {
        _uiState.update { it.copy(firstName = name, error = null) }
    }

    fun onLastNameChanged(name: String) {
        _uiState.update { it.copy(lastName = name, error = null) }
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        if (phoneNumber.length <= 10 && phoneNumber.all { it.isDigit() }) {
            _uiState.update { it.copy(phoneNumber = phoneNumber, error = null) }
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(address = address, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onUserTypeChanged(type: UserType) {
        _uiState.update { it.copy(userType = type) }
    }

    fun createAccount(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (!currentState.canRegister) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val request = RegisterRequest(
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                userType = currentState.userType.name,
                phoneNumber = currentState.phoneNumber,
                password = currentState.password,
                address = currentState.address
            )
            val result = repository.register(request)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                onSuccess()
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    fun resetRegistrationState() {
        _uiState.update { it.copy(isRegistered = false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                RegisterViewModel(application.authRepository)
            }
        }
    }
}
