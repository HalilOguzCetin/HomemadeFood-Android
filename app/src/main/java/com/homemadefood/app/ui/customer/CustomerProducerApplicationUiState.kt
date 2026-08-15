package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.ui.address.SelectedLocation

data class CustomerProducerApplicationUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,

    val application:
    ProducerApplicationStatusResponse? = null,

    val isFormVisible: Boolean = false,

    val businessName: String = "",
    val description: String = "",

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

    val isResolvingAddress: Boolean = false,

    val locationLookupMessage:
    String? = null,

    val dailyCapacityText: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isRejected: Boolean
        get() =
            application
                ?.verificationStatus
                ?.trim()
                ?.equals(
                    "Rejected",
                    ignoreCase = true
                ) == true

    val fullAddress: String
        get() = buildFullAddress()

    val canSubmit: Boolean
        get() =
            !isLoading &&
                    !isSubmitting &&
                    !isResolvingAddress &&
                    businessName.trim().length in 2..150 &&
                    description.trim().length in 10..1000 &&
                    city.isNotBlank() &&
                    district.isNotBlank() &&
                    neighborhood.isNotBlank() &&
                    street.isNotBlank() &&
                    buildingNo.isNotBlank() &&
                    selectedLocation
                        ?.isValid() == true &&
                    dailyCapacityText
                        .toIntOrNull()
                        ?.let { it in 1..1000 } == true

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

        return parts.joinToString(", ")
    }
}