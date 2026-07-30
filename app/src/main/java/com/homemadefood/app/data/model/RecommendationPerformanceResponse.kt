package com.homemadefood.app.data.model

data class RecommendationPerformanceResponse(
    val totalSearches: Int,
    val searchesWithRecommendations: Int,
    val searchesWithoutRecommendations: Int,
    val totalCandidatesShown: Int,
    val selectedSearches: Int,
    val recommendationOrders: Int,
    val deliveredOrders: Int,
    val cancelledOrders: Int,
    val rejectedOrders: Int,
    val reviewedOrders: Int,
    val searchToSelectionRate: Double,
    val selectionToOrderRate: Double,
    val orderDeliveryRate: Double,
    val reviewRate: Double,
    val averageSuitabilityScore: Double,
    val averageCustomerRating: Double,
    val averageSelectedRank: Double
)