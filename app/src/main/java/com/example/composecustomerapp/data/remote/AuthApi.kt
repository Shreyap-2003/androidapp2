package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.LoginRequest
import com.example.composecustomerapp.data.model.LoginResponse
import com.example.composecustomerapp.data.model.RegisterRequest
import com.example.composecustomerapp.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("application/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @GET("api/user/id/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UserResponse>
}
