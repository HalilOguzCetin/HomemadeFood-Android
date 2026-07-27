package com.homemadefood.app.ui.cart

import com.homemadefood.app.data.model.CartResponse

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: CartResponse? = null,

    val errorMessage: String? = null,
    val actionMessage: String? = null,

    val updatingCartItemId: Int? = null,
    val isClearingCart: Boolean = false
)