package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.OrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApi {
    @POST("api/orders")
    suspend fun placeOrder(@Body request: OrderRequest): Response<Unit>
}
