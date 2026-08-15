package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.ui.address.SelectedLocation

data class ProducerProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val profile:
    ProducerApplicationStatusResponse? = null,

    val isEditing: Boolean = false,

    val businessName: String = "",
    val description: String = "",

    /*
     * Yalnızca kullanıcının yeni seçtiği yerel URI.
     * Mevcut kayıtlı görsel profile.businessImageUrl içindedir.
     */
    val selectedBusinessImageUri:
    String? = null,

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
    val isAvailable: Boolean = false,

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val canEdit: Boolean
        get() =
            profile?.isApproved == true &&
                    profile.verificationStatus
                        .trim()
                        .equals(
                            "Approved",
                            ignoreCase = true
                        )

    val fullAddress: String
        get() = buildFullAddress()

    val canSave: Boolean
        get() =
            isEditing &&
                    !isSaving &&
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