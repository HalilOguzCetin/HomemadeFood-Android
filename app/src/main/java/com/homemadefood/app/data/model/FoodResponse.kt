package com.homemadefood.app.data.model

data class FoodResponse(
    val id: Int,
    val producerProfileId: Int,
    val businessName: String,
    val categoryId: Int,
    val categoryName: String,
    val name: String,
    val description: String,
    val ingredients: String = "",
    val price: Double,
    val unitType: String = "Portion",
    val dailyCapacity: Int = 1,
    val preparationTimeMinutes: Int,
    val minimumOrderLeadTimeMinutes: Int = 0,
    val allergenCodes: List<String> = emptyList(),
    val imageUrl: String,
    val isAvailable: Boolean,
    val isCurrentlyOpen: Boolean = false,
    val createdAt: String
)
