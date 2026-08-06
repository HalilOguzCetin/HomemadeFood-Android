package com.homemadefood.app.data.model

data class AdminOrderListItemResponse(
    val orderId: Int,

    val customerId: Int,
    val customerFullName: String,
    val customerEmail: String,

    val producerProfileId: Int,
    val businessName: String,

    val status: String,
    val paymentMethod: String,

    val totalPrice: Double,

    val itemCount: Int,
    val totalQuantity: Int,

    val recommendationSearchId: Int?,
    val suitabilityScore: Double,

    val createdAt: String,
    val statusUpdatedAt: String
)