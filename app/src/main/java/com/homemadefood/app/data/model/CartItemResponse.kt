package com.homemadefood.app.data.model

data class CartItemResponse(
    val cartItemId: Int,
    val foodId: Int,
    val foodName: String,
    val imageUrl: String,
    val unitPrice: Double,
    val unitType: String,
    val isUnitTypeCurrent: Boolean,
    val quantity: Int,
    val lineTotal: Double,
    val isAvailable: Boolean
)
