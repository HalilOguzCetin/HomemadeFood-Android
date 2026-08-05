package com.homemadefood.app.ui.customer

import com.homemadefood.app.data.model.ReviewResponse

data class CustomerReviewsUiState(
    val isLoading: Boolean = true,

    val reviews: List<ReviewResponse> =
        emptyList(),

    val reviewPendingDeletion:
    ReviewResponse? = null,

    val deletingReviewId: Int? = null,

    val errorMessage: String? = null,

    val successMessage: String? = null
) {
    val isDeleteDialogVisible: Boolean
        get() = reviewPendingDeletion != null

    val isEmpty: Boolean
        get() =
            !isLoading &&
                    reviews.isEmpty() &&
                    errorMessage == null
}