package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.authToken.first()
        }

        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("x-auth", it)
        }

        return chain.proceed(request.build())
    }
}
