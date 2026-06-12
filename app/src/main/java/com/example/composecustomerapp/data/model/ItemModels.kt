package com.example.composecustomerapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val createdDate: String? = null,
    val lastModifiedDate: String? = null,
    val subCategoryId: Int
)
