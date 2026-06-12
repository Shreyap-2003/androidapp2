package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.ItemResponse
import retrofit2.Response
import retrofit2.http.GET

interface ItemApi {
    @GET("api/items")
    suspend fun getItems(): Response<List<ItemResponse>>
}
