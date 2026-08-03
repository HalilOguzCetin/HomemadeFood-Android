package com.homemadefood.app.data.model

data class ProducerOrderResponse(
    val orderId: Int,

    val recommendationSearchId: Int?,
    val suitabilityScore: Double,

    val customerFullName: String,
    val customerPhone: String,

    val deliveryAddressTitle: String,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,

    val paymentMethod: String,
    val customerNote: String,

    val totalQuantity: Int,
    val totalPrice: Double,

    val status: String,

    val createdAt: String,
    val statusUpdatedAt: String,

    val items: List<OrderItemResponse>
) {
    val orderStatus: OrderStatus
        get() =
            OrderStatus.fromBackendValue(
                status
            )
}