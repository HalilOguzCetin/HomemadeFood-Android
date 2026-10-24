package com.homemadefood.app.data.model

data class ProducerStorefrontMenuFoodResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val unitType: String = "Portion",
    val preparationTimeMinutes: Int,
    val imageUrl: String
)