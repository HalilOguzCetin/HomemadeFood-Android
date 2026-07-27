package com.homemadefood.app.data.model

data class CartResponse(
    val cartId: Int?,
    val producerProfileId: Int?,
    val recommendationSearchId: Int?,
    val businessName: String,
    val items: List<CartItemResponse>,
    val totalQuantity: Int,
    val totalPrice: Double
)