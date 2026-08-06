package com.homemadefood.app.data.model

data class AdminOrderDetailResponse(
    val orderId: Int,

    val customerId: Int,
    val customerFullName: String,
    val customerEmail: String,
    val customerPhone: String,

    val producerProfileId: Int,
    val businessName: String,

    val status: String,
    val statusVersion: Int,

    val paymentMethod: String,
    val totalPrice: Double,

    val deliveryAddressTitle: String,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,

    val customerNote: String,

    val recommendationSearchId: Int?,
    val suitabilityScore: Double,

    val createdAt: String,
    val statusUpdatedAt: String,

    val items: List<AdminOrderItemResponse>
)