package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.local.TokenManager
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import java.util.concurrent.TimeUnit

object RetrofitClient {
private const val BASE_URL = "http://192.168.2.81:8080/"
//private const val BASE_URL = "http://10.0.2.2:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun createAuthApi(tokenManager: TokenManager): AuthApi {
        return createRetrofit(tokenManager).create(AuthApi::class.java)
    }

    fun createCategoryApi(tokenManager: TokenManager): CategoryApi {
        return createRetrofit(tokenManager).create(CategoryApi::class.java)
    }

    fun createItemApi(tokenManager: TokenManager): ItemApi {
        return createRetrofit(tokenManager).create(ItemApi::class.java)
    }

    fun createOrderApi(tokenManager: TokenManager): OrderApi {
        return createRetrofit(tokenManager).create(OrderApi::class.java)
    }

    private fun createRetrofit(tokenManager: TokenManager): Retrofit {
        val logging = HttpLoggingInterceptor { message: String ->
            android.util.Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
