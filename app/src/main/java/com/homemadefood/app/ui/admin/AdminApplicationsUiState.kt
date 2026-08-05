package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.model.ProducerApplicationStatus

data class AdminApplicationsUiState(
    val isLoading: Boolean = true,

    val selectedStatus:
    ProducerApplicationStatus =
        ProducerApplicationStatus.PENDING,

    val applications:
    List<AdminProducerApplicationResponse> =
        emptyList(),

    val updatingApplicationId: Int? = null,

    val successMessage: String? = null,

    val errorMessage: String? = null
) {
    val isPendingTab: Boolean
        get() =
            selectedStatus ==
                    ProducerApplicationStatus.PENDING

    val emptyMessage: String
        get() =
            when (selectedStatus) {
                ProducerApplicationStatus.PENDING ->
                    "Bekleyen üretici başvurusu bulunmuyor."

                ProducerApplicationStatus.APPROVED ->
                    "Onaylanmış üretici başvurusu bulunmuyor."

                ProducerApplicationStatus.REJECTED ->
                    "Reddedilmiş üretici başvurusu bulunmuyor."
            }
}