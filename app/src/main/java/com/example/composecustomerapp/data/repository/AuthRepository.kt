package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.model.LoginRequest
import com.example.composecustomerapp.data.model.LoginResponse
import com.example.composecustomerapp.data.model.RegisterRequest
import com.example.composecustomerapp.data.model.UserResponse
import com.example.composecustomerapp.data.remote.AuthApi

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun register(request: RegisterRequest): Result<UserResponse> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val token = response.headers()["x-auth"] ?: response.headers()["Authorization"]
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
                    val token = response.headers()["x-auth"] ?: response.headers()["Authorization"]
                    if (token != null) {
                        println("AuthDebug: Token found, saving to DataStore")
                        tokenManager.saveAuthToken(token)
                    } else {
                        println("AuthDebug: No x-auth or Authorization token in headers")
                    }
                    body.customerId?.let {
                        tokenManager.saveUserId(it.toString())
                    }
                    Result.success(body)
                } else {
                    println("AuthDebug: Body null or status not SUCCESS")
                    Result.failure(Exception(body?.message ?: "Login failed"))
                }
            } else {
                println("AuthDebug: HTTP Error: ${response.code()}")
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("AuthDebug: Exception in repository: ${e.message}")
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

    suspend fun logout() {
        tokenManager.clearAuthToken()
    }
}
