package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.AdminOrderListItemResponse

data class AdminOrdersUiState(
    val isLoading: Boolean = true,

    val orders:
    List<AdminOrderListItemResponse> =
        emptyList(),

    val selectedStatus:
    AdminOrderStatusFilter =
        AdminOrderStatusFilter.ALL,

    val searchQuery: String = "",

    val customerIdInput: String = "",

    val producerProfileIdInput: String = "",

    val dateFromInput: String = "",

    val dateToInput: String = "",

    val errorMessage: String? = null
) {
    val hasActiveFilters: Boolean
        get() =
            selectedStatus !=
                    AdminOrderStatusFilter.ALL ||
                    searchQuery.isNotBlank() ||
                    customerIdInput.isNotBlank() ||
                    producerProfileIdInput.isNotBlank() ||
                    dateFromInput.isNotBlank() ||
                    dateToInput.isNotBlank()

    val emptyMessage: String
        get() =
            if (hasActiveFilters) {
                "Seçilen filtrelere uygun sipariş bulunamadı."
            } else {
                "Sistemde kayıtlı sipariş bulunmuyor."
            }
}