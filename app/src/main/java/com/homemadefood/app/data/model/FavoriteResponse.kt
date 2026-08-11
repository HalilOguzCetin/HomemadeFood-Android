package com.homemadefood.app.data.model

data class FavoriteResponse(
    val favoriteId: Int,
    val foodId: Int,
    val producerProfileId: Int,
    val businessName: String,
    val categoryId: Int,
    val categoryName: String,
    val foodName: String,
    val price: Double,
    val imageUrl: String,
    val isAvailable: Boolean
)