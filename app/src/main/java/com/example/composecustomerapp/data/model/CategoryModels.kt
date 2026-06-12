package com.example.composecustomerapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val createdDate: String? = null,
    val lastModifiedDate: String? = null
)

@Serializable
data class SubCategoryResponse(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val createdDate: String? = null,
    val lastModifiedDate: String? = null,
    val categoryId: Int
)
