package com.homemadefood.app.ui.admin

import com.homemadefood.app.data.model.RecommendationPerformanceResponse

data class RecommendationAnalyticsUiState(
    val isLoading: Boolean = true,

    val analytics:
    RecommendationPerformanceResponse? = null,

    val errorMessage: String? = null
)