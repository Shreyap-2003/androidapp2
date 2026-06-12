package com.example.composecustomerapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val customerId: Int,
    val itemId: Int,
    val partnerId: Int,
    val orderStatus: String
)

@Serializable
data class OrderResponse(
    val id: Int? = null,
    val customerId: Int? = null,
    val itemId: Int? = null,
    val partnerId: Int? = null,
    val orderStatus: String? = null,
    val createdTime: String? = null
)
