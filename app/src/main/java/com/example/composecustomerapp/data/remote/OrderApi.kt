package com.example.composecustomerapp.data.remote

import com.example.composecustomerapp.data.model.OrderRequest
import com.example.composecustomerapp.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @POST("api/orders")
    suspend fun placeOrder(@Body request: com.example.composecustomerapp.data.model.PlaceOrderRequest): Response<Unit>

    @GET("api/orders/all")
    suspend fun getOrders(): Response<List<OrderResponse>>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<OrderResponse>

    @GET("api/orders")
    suspend fun getOrdersPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("customerId") customerId: Int? = null,
        @Query("partnerId") partnerId: Int? = null,
        @Query("status") status: String? = null
    ): Response<kotlinx.serialization.json.JsonElement>

    @GET("application/support/dispatches")
    suspend fun getDispatches(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<kotlinx.serialization.json.JsonElement>

    @GET("api/orders/partner/{partnerId}/completed-orders")
    suspend fun getPartnerCompletedOrders(
        @Path("partnerId") partnerId: Int,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<kotlinx.serialization.json.JsonElement>

    @GET("api/orders/customer/{customerId}/completed-orders")
    suspend fun getCustomerCompletedOrders(
        @Path("customerId") customerId: Int,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<kotlinx.serialization.json.JsonElement>

    @PUT("api/orders/{id}/assign-partner/{partnerId}")
    suspend fun assignPartner(
        @Path("id") id: Int,
        @Path("partnerId") partnerId: Int
    ): Response<OrderResponse>

    @PUT("api/orders/{id}/complete")
    suspend fun completeOrder(
        @Path("id") id: Int,
        @Query("partnerId") partnerId: Int
    ): Response<OrderResponse>
}
