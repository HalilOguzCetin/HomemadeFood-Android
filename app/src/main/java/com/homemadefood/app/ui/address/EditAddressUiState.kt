package com.homemadefood.app.ui.address

data class EditAddressUiState(
    val isLoading: Boolean = true,

    val title: String = "",
    val fullAddress: String = "",
    val selectedLocation: SelectedLocation? = null,
    val isDefault: Boolean = false,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,

    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() =
            !isLoading &&
                    !isSaving &&
                    title.isNotBlank() &&
                    fullAddress.isNotBlank() &&
                    selectedLocation?.isValid() == true
}