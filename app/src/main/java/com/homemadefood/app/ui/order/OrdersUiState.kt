package com.homemadefood.app.ui.order

import com.homemadefood.app.data.model.OrderResponse

data class OrdersUiState(
    val isLoading: Boolean = true,

    val orders: List<OrderResponse> =
        emptyList(),

    val cancellingOrderId: Int? = null,

    val errorMessage: String? = null,

    val actionMessage: String? = null
)