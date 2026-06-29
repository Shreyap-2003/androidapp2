package com.example.composecustomerapp.data.remote

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
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
    @POST("application/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @GET("api/user/id/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UserResponse>

    @GET("api/user")
    suspend fun getUsers(): Response<List<UserResponse>>

    @POST("application/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // 👇 New: update FCM token
    @PUT("api/fcm-token")
    suspend fun updateFcmToken(
        @Query("id") id: Long,
        @Body request: FCMDto
    ): Response<ResponseBody>
}