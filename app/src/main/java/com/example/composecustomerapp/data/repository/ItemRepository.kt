package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.model.ItemResponse
import com.example.composecustomerapp.data.remote.ItemApi

class ItemRepository(
    private val itemApi: ItemApi
) {
    suspend fun getItems(): Result<List<ItemResponse>> {
        return try {
            val response = itemApi.getItems()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemsBySubcategory(subcategoryId: Int): Result<List<ItemResponse>> {
        return try {
            val response = itemApi.getItemsBySubcategory(subcategoryId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
