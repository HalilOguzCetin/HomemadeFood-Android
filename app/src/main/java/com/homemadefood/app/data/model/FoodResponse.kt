package com.homemadefood.app.data.model

data class FoodResponse(
    val id: Int,
    val producerProfileId: Int,
    val businessName: String,
    val categoryId: Int,
    val categoryName: String,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTimeMinutes: Int,
    val imageUrl: String,
    val isAvailable: Boolean,
    val createdAt: String
)