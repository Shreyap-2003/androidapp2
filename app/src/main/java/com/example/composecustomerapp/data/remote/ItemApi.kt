package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.ItemResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApi {
    @GET("api/items")
    suspend fun getItems(): Response<List<ItemResponse>>

    @GET("api/items/subcategory/{subcategoryId}")
    suspend fun getItemsBySubcategory(@Path("subcategoryId") subcategoryId: Int): Response<List<ItemResponse>>
}
