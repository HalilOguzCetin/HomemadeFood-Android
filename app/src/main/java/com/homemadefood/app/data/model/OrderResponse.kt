package com.homemadefood.app.data.model

data class OrderResponse(
    val orderId: Int,
    val producerProfileId: Int,
    val businessName: String,

    val recommendationSearchId: Int?,
    val suitabilityScore: Double,

    val deliveryAddressTitle: String,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,

    val paymentMethod: String,
    val customerNote: String,

    val totalPrice: Double,
    val status: String,

    val createdAt: String,
    val statusUpdatedAt: String,

    val items: List<OrderItemResponse>
)