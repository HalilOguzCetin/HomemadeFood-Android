package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.AdminUserListItemResponse

data class AdminUsersUiState(
    val isLoading: Boolean = true,

    val users:
    List<AdminUserListItemResponse> =
        emptyList(),

    val selectedRoleFilter:
    AdminUserRoleFilter =
        AdminUserRoleFilter.ALL,

    val selectedStatusFilter:
    AdminUserStatusFilter =
        AdminUserStatusFilter.ALL,

    val searchQuery: String = "",

    val errorMessage: String? = null
) {
    val emptyMessage: String
        get() {
            return if (
                searchQuery.isNotBlank() ||
                selectedRoleFilter !=
                AdminUserRoleFilter.ALL ||
                selectedStatusFilter !=
                AdminUserStatusFilter.ALL
            ) {
                "Seçilen filtrelere uygun kullanıcı bulunamadı."
            } else {
                "Sistemde kayıtlı kullanıcı bulunmuyor."
            }
        }
}