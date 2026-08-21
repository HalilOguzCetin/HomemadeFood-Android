package com.homemadefood.app.data.model

data class DiscoverFoodResponse(
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

    val createdAt: String,

    val distanceKm: Double = 0.0,
    val popularityScore: Double = 0.0
)