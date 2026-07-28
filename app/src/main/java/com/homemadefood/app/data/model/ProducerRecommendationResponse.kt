package com.homemadefood.app.data.model

data class ProducerRecommendationResponse(
    val foodId: Int,
    val foodName: String,
    val foodDescription: String?,
    val price: Double,
    val preparationTimeMinutes: Int,

    val producerProfileId: Int,
    val businessName: String,

    val averageRating: Double,
    val reviewCount: Int,
    val remainingCapacity: Int,
    val distanceKm: Double,

    val ratingScore: Double,
    val distanceScore: Double,
    val preparationScore: Double,
    val totalScore: Double,

    val explanation: String
)