package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.CategoryResponse
import com.example.composecustomerapp.data.model.SubCategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {
    @GET("api/category")
    suspend fun getCategories(): Response<List<CategoryResponse>>

    @GET("api/subcategory")
    suspend fun getSubCategories(): Response<List<SubCategoryResponse>>
}
