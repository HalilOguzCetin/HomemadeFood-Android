package com.homemadefood.app.ui.address

import com.homemadefood.app.data.model.AddressResponse

data class AddressesUiState(
    val isLoading: Boolean = false,
    val addresses: List<AddressResponse> = emptyList(),
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val deletingAddressId: Int? = null
)