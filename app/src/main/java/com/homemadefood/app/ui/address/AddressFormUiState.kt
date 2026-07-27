package com.homemadefood.app.ui.address

data class AddressFormUiState(
    val title: String = "",
    val fullAddress: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val isDefault: Boolean = false,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,

    val errorMessage: String? = null
)