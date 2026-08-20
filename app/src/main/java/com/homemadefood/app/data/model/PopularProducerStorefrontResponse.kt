package com.homemadefood.app.data.model

data class PopularProducerStorefrontResponse(
    val producerProfileId: Int,
    val businessName: String,
    val description: String,
    val businessImageUrl: String? = null,
    val rating: Double = 0.0,
    val city: String,
    val district: String,
    val availableFoodCount: Int,
    val availableCategoryCount: Int,
    val matchingFoodCount: Int,
    val minimumPreparationTimeMinutes: Int? = null,

    val popularityScore: Double = 0.0,
    val deliveredOrderCount30Days: Int = 0,
    val distinctCustomerCount30Days: Int = 0,
    val reviewCount: Int = 0,
    val favoriteCount: Int = 0
)