package com.homemadefood.app.ui.order

import com.homemadefood.app.data.model.OrderResponse
import com.homemadefood.app.data.model.ReviewResponse

data class OrderDetailUiState(
    val isLoading: Boolean = true,

    val order: OrderResponse? = null,

    val isCancelling: Boolean = false,

    val isReviewStatusLoading: Boolean = false,
    val hasCheckedReview: Boolean = false,

    val existingReview: ReviewResponse? = null,

    val isReviewFormVisible: Boolean = false,
    val selectedRating: Int = 0,
    val reviewComment: String = "",
    val isSubmittingReview: Boolean = false,

    val errorMessage: String? = null,

    val actionMessage: String? = null
)