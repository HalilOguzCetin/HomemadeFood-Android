package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.AdminOrderDetailResponse

data class AdminOrderDetailUiState(
    val isLoading: Boolean = true,

    val order: AdminOrderDetailResponse? = null,

    val errorMessage: String? = null
)