package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.model.OrderRequest
import com.example.composecustomerapp.data.remote.OrderApi

class OrderRepository(
    private val orderApi: OrderApi
) {
    suspend fun placeOrder(customerId: Int, itemId: Int): Result<Unit> {
        return try {
            val response = orderApi.placeOrder(OrderRequest(customerId, itemId, 1, "OPEN"))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to place order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
