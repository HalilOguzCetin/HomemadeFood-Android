package com.homemadefood.app.data.model

data class CreateFoodRequest(
    val categoryId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTimeMinutes: Int,
    val imageUrl: String
)