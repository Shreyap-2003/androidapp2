package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.model.FCMDto
import com.example.composecustomerapp.data.model.ForgotPasswordRequest
import com.example.composecustomerapp.data.model.ForgotPasswordResponse
import com.example.composecustomerapp.data.model.LoginRequest
import com.example.composecustomerapp.data.model.LoginResponse
import com.example.composecustomerapp.data.model.RegisterRequest
import com.example.composecustomerapp.data.model.ResetPasswordRequest
import com.example.composecustomerapp.data.model.ResetPasswordResponse
import com.example.composecustomerapp.data.model.UserResponse
import com.example.composecustomerapp.data.model.VerifyOtpRequest
import com.example.composecustomerapp.data.model.VerifyOtpResponse
import com.example.composecustomerapp.data.remote.AuthApi

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        return try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Invalid Email address"))
                }
            } else {
                Result.failure(Exception("Invalid Email address"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> {
        return try {
            val response = authApi.verifyOtp(VerifyOtpRequest(email, otp))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val token = response.headers()["x-auth"] ?: response.headers()["Authorization"]
                    if (token != null) {
                        tokenManager.saveAuthToken(token)
                    }
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Invalid OTP"))
                }
            } else {
                Result.failure(Exception("Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<ResetPasswordResponse> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequest(email, newPassword))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to reset password"))
                }
            } else {
                Result.failure(Exception("Failed to reset password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<UserResponse> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val token = response.headers()["X-Auth-Token"] 
                        ?: response.headers()["x-auth-token"]
                        ?: response.headers()["x-auth"] 
                        ?: response.headers()["Authorization"]
                    
                    if (token != null) {
                        tokenManager.saveAuthToken(token)
                    }
                    body.id?.let {
                        tokenManager.saveUserId(it.toString())
                    }
                    Result.success(body)
                } else {
                    Result.failure(Exception("Registration failed: Empty response"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(phoneNumber: String, password: String): Result<LoginResponse> {
        println("AuthDebug: Repository login called for $phoneNumber")
        return try {
            val response = authApi.login(LoginRequest(phoneNumber, password))
            println("AuthDebug: Response received. Code: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                println("AuthDebug: Response body: $body")
                if (body != null && body.status == "SUCCESS") {
                    val token = response.headers()["X-Auth-Token"] 
                        ?: response.headers()["x-auth-token"]
                        ?: response.headers()["x-auth"] 
                        ?: response.headers()["Authorization"]
                    
                    if (token != null) {
                        println("AuthDebug: Token found: $token")
                        tokenManager.saveAuthToken(token)
                    }

                    val userId = body.customerId?.toLong() ?: body.id
                    userId?.let {
                        tokenManager.saveUserId(it.toString())
                    }
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Login failed"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserResponse> {
        return try {
            val response = authApi.getUserProfile(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: UserResponse())
            } else {
                Result.failure(Exception("Error fetching profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Result<List<UserResponse>> {
        return try {
            val response = authApi.getUsers()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            tokenManager.clearAuthToken()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            tokenManager.clearAuthToken()
            Result.failure(e)
        }
    }

    suspend fun updateFcmToken(
        id: Long,
        fcmToken: String
    ): Result<Unit> {
        return try {
            val response = authApi.updateFcmToken(
                id = id,
                request = FCMDto(fcmToken)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("FCM update failed ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
