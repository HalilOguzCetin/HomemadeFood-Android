package com.homemadefood.app.ui.order

import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CartResponse
import com.homemadefood.app.data.model.OrderResponse
import com.homemadefood.app.data.model.PaymentMethods

data class CreateOrderUiState(
    val isLoading: Boolean = true,

    val cart: CartResponse? = null,

    val addresses: List<AddressResponse> =
        emptyList(),

    val selectedAddressId: Int? = null,

    val paymentMethod: String =
        PaymentMethods.CASH_ON_DELIVERY,

    val customerNote: String = "",

    val isCreatingOrder: Boolean = false,

    val createdOrder: OrderResponse? = null,

    val errorMessage: String? = null
)