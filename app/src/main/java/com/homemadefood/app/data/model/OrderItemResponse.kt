package com.homemadefood.app.data.model

data class OrderItemResponse(
    val orderItemId: Int,
    val foodId: Int,
    val foodName: String,
    val quantity: Int,
    val unitType: String,
    val unitPrice: Double,
    val totalPrice: Double
)
