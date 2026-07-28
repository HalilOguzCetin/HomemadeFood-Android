package com.homemadefood.app.data.model

data class ProducerRecommendationSelectionResponse(
    val recommendationSearchId: Int,
    val foodId: Int,
    val foodName: String,

    val producerProfileId: Int,
    val businessName: String,

    val rank: Int,
    val totalScore: Double,
    val selectedAtUtc: String
)