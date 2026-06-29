package com.example.composecustomerapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceOrderRequest(
    val customerId: Int,
    val itemId: Int,
    val orderStatus: String
)

@Serializable
data class OrderRequest(
    val customerId: Int,
    val itemId: Int,
    val partnerId: Int? = null,
    val orderStatus: String
)

@Serializable
data class OrderResponse(
    val id: Int? = null,
    val customerId: Int? = null,
    val itemId: Int? = null,
    val partnerId: Int? = null,
    val orderStatus: String? = null,
    val createdTime: String? = null,
    // Dispatch fields from paginated endpoint
    val jobId: Int? = null,
    val dispatchStatus: String? = null,
    val lastModifiedTime: String? = null,
    val distanceInKms: Double? = null,
    // Completed order decorated fields
    val itemName: String? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val completedTime: String? = null,
    // Customer details
    val customerName: String? = null,    // ← add
    val customerPhone: String? = null,   // ← add
    val customerAddress: String? = null  // ← add
)

@Serializable
data class PageResponse<T>(
    val content: List<T>,
    val page: PageInfo
)

@Serializable
data class PageInfo(
    val size: Int,
    val number: Int,
    val totalElements: Long,
    val totalPages: Int
)

data class Order(
    val id: String,
    val orderNumber: String,
    val name: String,
    val price: Double,
    val status: String,
    val imageUrl: String,
    val date: String,
    val partnerId: Int? = null,
    val partnerName: String? = null,
    val partnerPhoneNumber: String? = null,
    val customerName: String? = null,
    val customerPhoneNumber: String? = null,
    val customerAddress: String? = null,
    val customerLatitude: Double? = null,
    val customerLongitude: Double? = null
)
