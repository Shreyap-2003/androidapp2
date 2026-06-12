package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.model.CategoryResponse
import com.example.composecustomerapp.data.model.SubCategoryResponse
import com.example.composecustomerapp.data.remote.CategoryApi

class CategoryRepository(
    private val categoryApi: CategoryApi
) {
    suspend fun getCategories(): Result<List<CategoryResponse>> {
        return try {
            val response = categoryApi.getCategories()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubCategories(): Result<List<SubCategoryResponse>> {
        return try {
            val response = categoryApi.getSubCategories()
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
