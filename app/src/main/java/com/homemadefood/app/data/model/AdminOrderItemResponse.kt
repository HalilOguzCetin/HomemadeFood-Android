package com.homemadefood.app.data.model

data class AdminOrderItemResponse(
    val orderItemId: Int,
    val foodId: Int,
    val foodName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)