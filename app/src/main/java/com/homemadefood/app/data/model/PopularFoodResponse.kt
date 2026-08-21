package com.homemadefood.app.data.model

data class PopularFoodResponse(
    val popularityScore: Double = 0.0,
    val deliveredOrderCount30Days: Int = 0,
    val soldQuantity30Days: Int = 0,
    val distinctCustomerCount30Days: Int = 0,
    val favoriteCount: Int = 0,

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