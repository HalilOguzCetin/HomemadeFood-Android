package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.AdminUserDetailResponse

data class AdminUserDetailUiState(
    val isLoading: Boolean = true,

    val user: AdminUserDetailResponse? = null,

    val isUpdatingStatus: Boolean = false,

    val successMessage: String? = null,

    val errorMessage: String? = null
)