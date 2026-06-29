package com.example.composecustomerapp.data.repository

import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.data.model.OrderResponse
import com.example.composecustomerapp.data.remote.AuthApi
import com.example.composecustomerapp.data.remote.ItemApi
import com.example.composecustomerapp.data.remote.OrderApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.composecustomerapp.data.paging.OrderPagingSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderRepository(
    private val orderApi: OrderApi,
    private val itemApi: ItemApi,
    private val authApi: AuthApi
) {
    suspend fun placeOrder(customerId: Int, itemId: Int): Result<Unit> {
        return try {
            val response = orderApi.placeOrder(com.example.composecustomerapp.data.model.PlaceOrderRequest(customerId, itemId, "OPEN"))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    fun getOrdersPaged(
        customerId: Int? = null,
        partnerId: Int? = null,
        status: String? = null
    ): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10,
                prefetchDistance = 1
            ),
            pagingSourceFactory = {
                OrderPagingSource(
                    fetchOrders = { page, size ->
                        if (status == "COMPLETED") {
                            if (partnerId != null) orderApi.getPartnerCompletedOrders(partnerId, page, size)
                            else if (customerId != null) orderApi.getCustomerCompletedOrders(customerId, page, size)
                            else orderApi.getOrdersPaginated(page, size, customerId, partnerId, status)
                        } else {
                            orderApi.getOrdersPaginated(page, size, customerId, partnerId, status)
                        }
                    },
                    mapToDomain = { responses ->
                        if (responses.isEmpty()) return@OrderPagingSource emptyList()

                        val isDecorated = responses.any { it.itemName != null }
                        val isCustomer = customerId != null

                        if (isDecorated) {
                            // Backend already filters — map directly, no local filtering
                            responses.map { resp ->
                                val finalStatus = if (isCustomer) {
                                    when (resp.orderStatus) {
                                        "OPEN" -> "IN_PROGRESS"
                                        "IN_PROGRESS" -> "ASSIGNED"
                                        else -> resp.orderStatus ?: "COMPLETED"
                                    }
                                } else resp.orderStatus ?: "COMPLETED"

                                Order(
                                    id = resp.id.toString(),
                                    orderNumber = resp.id.toString(),
                                    name = resp.itemName ?: "Unknown",
                                    price = resp.price ?: 0.0,
                                    status = finalStatus,
                                    imageUrl = resp.imageUrl ?: "",
                                    date = formatDate(resp.completedTime ?: resp.createdTime),
                                    partnerId = resp.partnerId ?: partnerId,
                                    customerName = resp.customerName,       // ← was null
                                    customerPhoneNumber = resp.customerPhone, // ← was null
                                    customerAddress = resp.customerAddress  // ← was null
                                )
                            }
                        } else {
                            // Generic endpoint — still needs local filtering as server may not filter
                            val filtered = responses.filter { resp ->
                                val matchesCustomer = customerId == null || resp.customerId == customerId
                                val matchesPartner = partnerId == null || resp.partnerId == partnerId
                                val matchesStatus = if (status != null) {
                                    when (status) {
                                        "ACTIVE" -> resp.orderStatus in listOf("OPEN", "ASSIGNED", "IN_PROGRESS")
                                        "COMPLETED" -> resp.orderStatus == "COMPLETED" || resp.completedTime != null
                                        else -> resp.orderStatus == status
                                    }
                                } else true
                                matchesCustomer && matchesPartner && matchesStatus
                            }
                            val items = itemApi.getItems().body() ?: emptyList()
                            mapResponsesToOrders(filtered, items, isCustomerSide = isCustomer)
                        }
                    }
                )
            }
        ).flow
    }

    suspend fun getOrders(customerId: Int): Result<List<Order>> {
        return try {
            val response = orderApi.getOrders()
            if (response.isSuccessful) {
                val orderResponses = (response.body() ?: emptyList()).filter { it.customerId == customerId }
                val items = itemApi.getItems().body() ?: emptyList()
                Result.success(mapResponsesToOrders(orderResponses, items, isCustomerSide = true))
            } else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getOrderById(orderId: Int): Result<Order?> {
        return try {
            val response = orderApi.getOrderById(orderId)
            if (response.isSuccessful) {
                val orderResp = response.body() ?: return Result.success(null)
                val items = itemApi.getItems().body() ?: emptyList()
                val orders = mapResponsesToOrders(listOf(orderResp), items, isCustomerSide = true)
                Result.success(orders.firstOrNull())
            } else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAllOpenOrders(): Result<List<Order>> {
        return try {
            val response = orderApi.getOrders()
            if (response.isSuccessful) {
                val openOrders = (response.body() ?: emptyList()).filter { it.orderStatus == "OPEN" }
                val items = itemApi.getItems().body() ?: emptyList()
                Result.success(mapResponsesToOrders(openOrders, items))
            } else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPartnerActiveOrders(partnerId: Int): Result<List<Order>> {
        return try {
            val response = orderApi.getOrders()
            if (response.isSuccessful) {
                val active = (response.body() ?: emptyList()).filter { it.partnerId == partnerId && it.orderStatus == "IN_PROGRESS" }
                val items = itemApi.getItems().body() ?: emptyList()
                Result.success(mapResponsesToOrders(active, items))
            } else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    private suspend fun mapResponsesToOrders(
        orderResponses: List<OrderResponse>,
        items: List<com.example.composecustomerapp.data.model.ItemResponse>,
        isCustomerSide: Boolean = false
    ): List<Order> = coroutineScope {
        orderResponses.map { orderResp ->
            async {
                val item = items.find { it.id == orderResp.itemId }
                var cName: String? = null; var cPhone: String? = null; var addr: String? = null
                var lat: Double? = null; var lng: Double? = null
                var pName: String? = null; var pPhone: String? = null

                try {
                    val cResp = authApi.getUserProfile(orderResp.customerId.toString())
                    if (cResp.isSuccessful) {
                        val c = cResp.body()
                        cName = "${c?.firstName} ${c?.lastName}"; cPhone = c?.phoneNumber; addr = c?.address
                        lat = c?.latitude; lng = c?.longitude
                    }
                    if (orderResp.orderStatus == "IN_PROGRESS" && orderResp.partnerId != null) {
                        val pResp = authApi.getUserProfile(orderResp.partnerId.toString())
                        if (pResp.isSuccessful) {
                            val p = pResp.body()
                            pName = "${p?.firstName} ${p?.lastName}"; pPhone = p?.phoneNumber
                        }
                    }
                } catch (e: Exception) {}

                val status = if (isCustomerSide) {
                    when (orderResp.orderStatus) {
                        "OPEN" -> "IN_PROGRESS"
                        "IN_PROGRESS" -> "ASSIGNED"
                        else -> orderResp.orderStatus ?: "UNKNOWN"
                    }
                } else orderResp.orderStatus ?: "UNKNOWN"

                Order(
                    id = orderResp.id.toString(), orderNumber = orderResp.id.toString(),
                    name = orderResp.itemName ?: item?.name ?: "Unknown Item",
                    price = orderResp.price ?: item?.price?.toDouble() ?: 0.0,
                    status = status, imageUrl = orderResp.imageUrl ?: item?.imageUrl ?: "",
                    date = formatDate(orderResp.completedTime ?: orderResp.createdTime),
                    partnerId = orderResp.partnerId, partnerName = pName, partnerPhoneNumber = pPhone,
                    customerName = cName, customerPhoneNumber = cPhone, customerAddress = addr,
                    customerLatitude = lat, customerLongitude = lng
                )
            }
        }.awaitAll()
    }

    suspend fun assignPartner(orderId: Int, partnerId: Int): Result<OrderResponse> {
        return try {
            val response = orderApi.assignPartner(orderId, partnerId)
            if (response.isSuccessful) Result.success(response.body() ?: OrderResponse())
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun completeOrder(orderId: Int, partnerId: Int): Result<OrderResponse> {
        return try {
            val response = orderApi.completeOrder(orderId, partnerId)
            if (response.isSuccessful) Result.success(response.body() ?: OrderResponse())
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr == null) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateStr)
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) { dateStr }
    }
}