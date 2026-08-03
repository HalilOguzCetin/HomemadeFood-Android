package com.homemadefood.app.ui.producer

import com.homemadefood.app.data.model.ReviewResponse

data class ProducerReviewsUiState(
    val isLoading: Boolean = true,

    val producerProfileId: Int? = null,

    val reviews: List<ReviewResponse> =
        emptyList(),

    val errorMessage: String? = null
) {
    val totalReviewCount: Int
        get() = reviews.size

    val averageRating: Double
        get() {
            if (reviews.isEmpty()) {
                return 0.0
            }

            return reviews
                .map { review ->
                    review.rating
                }
                .average()
        }
}