package com.homemadefood.app.ui.address

data class AddressFormUiState(
    val title: String = "",

    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",
    val floor: String = "",
    val apartmentNo: String = "",
    val addressNote: String = "",

    val selectedLocation:
    SelectedLocation? = null,

    val isResolvingAddress:
    Boolean = false,

    val locationLookupMessage:
    String? = null,

    val isDefault: Boolean = false,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,

    val errorMessage: String? = null
) {
    val fullAddress: String
        get() = buildFullAddress()

    val canSave: Boolean
        get() =
            !isSaving &&
                    !isResolvingAddress &&
                    title.isNotBlank() &&
                    city.isNotBlank() &&
                    district.isNotBlank() &&
                    neighborhood.isNotBlank() &&
                    street.isNotBlank() &&
                    buildingNo.isNotBlank() &&
                    selectedLocation
                        ?.isValid() == true

    fun buildFullAddress(): String {
        val parts =
            mutableListOf<String>()

        if (neighborhood.isNotBlank()) {
            parts += neighborhood.trim()
        }

        if (street.isNotBlank()) {
            parts += street.trim()
        }

        if (buildingNo.isNotBlank()) {
            parts +=
                "No: ${buildingNo.trim()}"
        }

        if (floor.isNotBlank()) {
            parts +=
                "Kat: ${floor.trim()}"
        }

        if (apartmentNo.isNotBlank()) {
            parts +=
                "Daire: ${apartmentNo.trim()}"
        }

        val districtCity =
            listOf(
                district.trim(),
                city.trim()
            )
                .filter {
                    it.isNotBlank()
                }
                .joinToString("/")

        if (districtCity.isNotBlank()) {
            parts += districtCity
        }

        if (addressNote.isNotBlank()) {
            parts +=
                "Tarif: ${addressNote.trim()}"
        }

        return parts
            .joinToString(", ")
    }
}