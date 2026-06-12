package com.example.composecustomerapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val status: String? = null,
    val name: String? = null,
    val customerId: Int? = null,
    val message: String? = null,
    val userType: String? = null
)

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val userType: String = "CUSTOMER",
    val phoneNumber: String,
    val password: String,
    val address: String
)

@Serializable
data class UserResponse(
    val id: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val userType: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
