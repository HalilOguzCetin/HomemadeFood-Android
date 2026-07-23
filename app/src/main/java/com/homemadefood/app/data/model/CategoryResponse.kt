package com.homemadefood.app.data.model

data class CategoryResponse(
    val id: Int,
    val name: String,
    val description: String,
    val isActive: Boolean? = null
)