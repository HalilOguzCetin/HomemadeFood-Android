package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.ProducerApplicationStatus
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerComplianceResponse
import com.homemadefood.app.ui.address.SelectedLocation

data class CustomerProducerApplicationUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,

    val application:
    ProducerApplicationStatusResponse? = null,

    val isFormVisible: Boolean = false,

    val businessName: String = "",
    val description: String = "",

    val selectedBusinessImageUri:
    String? = null,

    val existingBusinessImageUrl:
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

    val isComplianceLoading: Boolean = false,
    val isComplianceSaving: Boolean = false,

    val compliance:
    ProducerComplianceResponse? = null,

    val isComplianceFormVisible: Boolean = false,

    val complianceTaxStatus: String = "NotDeclared",
    val complianceTaxNumber: String = "",
    val complianceTaxExemptionCertificateNumber: String = "",
    val complianceFoodBusinessRegistrationNumber: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val applicationStatus: ProducerApplicationStatus?
        get() =
            ProducerApplicationStatus
                .fromBackendValue(
                    application
                        ?.verificationStatus
                )

    val isRejected: Boolean
        get() =
            applicationStatus ==
                    ProducerApplicationStatus.REJECTED

    val requiresAdditionalComplianceInformation: Boolean
        get() =
            applicationStatus ==
                    ProducerApplicationStatus
                        .ADDITIONAL_DOCUMENT_REQUIRED

    val canEditCompliance: Boolean
        get() =
            application != null &&
                    (
                            applicationStatus ==
                                    ProducerApplicationStatus.PENDING ||
                                    applicationStatus ==
                                    ProducerApplicationStatus
                                        .ADDITIONAL_DOCUMENT_REQUIRED
                            )

    val fullAddress: String
        get() = buildFullAddress()

    val hasBusinessImage: Boolean
        get() =
            !selectedBusinessImageUri
                .isNullOrBlank() ||
                    !existingBusinessImageUrl
                        .isNullOrBlank()

    val canSubmit: Boolean
        get() =
            !isLoading &&
                    !isSubmitting &&
                    !isResolvingAddress

    val canSaveCompliance: Boolean
        get() =
            canEditCompliance &&
                    !isComplianceLoading &&
                    !isComplianceSaving

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
