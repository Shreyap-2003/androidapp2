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
    val loginSuccess: Boolean = false,
    val forgotPasswordEmail: String = "",
    val isForgotPasswordLoading: Boolean = false,
    val forgotPasswordError: String? = null,
    val forgotPasswordSuccessMessage: String? = null,
    val otp: String = "",
    val isVerifyOtpLoading: Boolean = false,
    val verifyOtpError: String? = null,
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val newPassword: String = "",
    val isResetPasswordLoading: Boolean = false,
    val resetPasswordError: String? = null
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

    fun onForgotPasswordEmailChanged(email: String) {
        _uiState.update { it.copy(forgotPasswordEmail = email, forgotPasswordError = null) }
    }

    fun onOtpChanged(otp: String) {
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _uiState.update { it.copy(otp = otp, verifyOtpError = null) }
        }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, resetPasswordError = null) }
    }

    fun resetForgotPasswordState() {
        _uiState.update {
            it.copy(
                forgotPasswordEmail = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                otp = "",
                verifyOtpError = null,
                isOtpSent = false,
                isOtpVerified = false,
                newPassword = "",
                isResetPasswordLoading = false,
                resetPasswordError = null
            )
        }
    }

    fun forgotPassword(onSuccess: (String) -> Unit) {
        val email = _uiState.value.forgotPasswordEmail
        if (email.isEmpty()) {
            _uiState.update { it.copy(forgotPasswordError = "Please enter email") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isForgotPasswordLoading = true, forgotPasswordError = null) }
            val result = repository.forgotPassword(email)
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(isForgotPasswordLoading = false, forgotPasswordSuccessMessage = response.message, isOtpSent = true)
                }
                onSuccess(response.message ?: "OTP sent successfully")
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isForgotPasswordLoading = false, forgotPasswordError = exception.message ?: "Invalid Email address")
                }
            }
        }
    }

    fun verifyOtp(onSuccess: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState.otp.isEmpty()) {
            _uiState.update { it.copy(verifyOtpError = "Please enter OTP") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifyOtpLoading = true, verifyOtpError = null) }
            val result = repository.verifyOtp(currentState.forgotPasswordEmail, currentState.otp)
            result.onSuccess { response ->
                _uiState.update { it.copy(isVerifyOtpLoading = false, isOtpVerified = true) }
                onSuccess(response.message ?: "OTP verified successfully")
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isVerifyOtpLoading = false, verifyOtpError = exception.message ?: "Invalid OTP")
                }
            }
        }
    }

    fun resetPassword(onSuccess: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState.newPassword.length < 5) {
            _uiState.update { it.copy(resetPasswordError = "Password must be at least 5 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isResetPasswordLoading = true, resetPasswordError = null) }
            val result = repository.resetPassword(currentState.forgotPasswordEmail, currentState.newPassword)
            result.onSuccess { response ->
                _uiState.update { it.copy(isResetPasswordLoading = false) }
                onSuccess(response.message ?: "Password reset successfully")
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isResetPasswordLoading = false, resetPasswordError = exception.message ?: "Failed to reset password")
                }
            }
        }
    }

    // 👇 passes both userType and userId to LoginScreen
    fun signIn(onSuccess: (userType: String, userId: Long) -> Unit) {
        val currentState = _uiState.value
        if (!currentState.canSignIn) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.login(currentState.phoneNumber, currentState.password)

            result.onSuccess { loginResponse ->
                println("AuthDebug: signIn() success, userType: ${loginResponse.userType}")
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                val userId = loginResponse.customerId?.toLong() ?: 0L
                onSuccess(loginResponse.userType ?: "CUSTOMER", userId)  // 👈 both passed
            }.onFailure { exception ->
                println("AuthDebug: signIn() failure: ${exception.message}")
                _uiState.update { it.copy(isLoading = false, error = "invalid phone number or password") }
            }
        }
    }

    // 👇 Send FCM token to backend using user id
    fun updateFcmToken(userId: Long, fcmToken: String) {
        viewModelScope.launch {
            try {
                repository.updateFcmToken(userId, fcmToken)
                println("AuthDebug: FCM token updated successfully")
            } catch (e: Exception) {
                println("AuthDebug: Failed to update FCM token: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                LoginViewModel(application.authRepository)
            }
        }
    }
}