package com.homemadefood.app.data.model

data class ProducerStorefrontMenuResponse(
    val producerProfileId: Int,
    val businessName: String,
    val description: String,
    val businessImageUrl: String? = null,
    val rating: Double = 0.0,
    val city: String,
    val district: String,
    val availableFoodCount: Int,
    val availableCategoryCount: Int,
    val categories: List<ProducerStorefrontMenuCategoryResponse>
)