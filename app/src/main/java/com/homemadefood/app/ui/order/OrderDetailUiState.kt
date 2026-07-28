package com.homemadefood.app.ui.order

import com.homemadefood.app.data.model.OrderResponse

data class OrderDetailUiState(
    val isLoading: Boolean = true,

    val order: OrderResponse? = null,

    val isCancelling: Boolean = false,

    val errorMessage: String? = null,

    val actionMessage: String? = null
)