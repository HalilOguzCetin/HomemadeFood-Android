package com.homemadefood.app.data.model

data class CreateOrderRequest(
    val addressId: Int,
    val paymentMethod: String,
    val customerNote: String
)