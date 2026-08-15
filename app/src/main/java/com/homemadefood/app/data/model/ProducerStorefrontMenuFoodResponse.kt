package com.homemadefood.app.data.model

data class ProducerStorefrontMenuFoodResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTimeMinutes: Int,
    val imageUrl: String
)