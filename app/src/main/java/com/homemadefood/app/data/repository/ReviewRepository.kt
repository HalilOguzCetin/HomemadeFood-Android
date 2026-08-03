package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateReviewRequest
import com.homemadefood.app.data.model.DeleteReviewResponse
import com.homemadefood.app.data.model.ReviewResponse
import com.homemadefood.app.data.remote.RetrofitClient
import com.homemadefood.app.data.remote.ReviewApiService
import retrofit2.Response

class ReviewRepository(
    private val reviewApiService:
    ReviewApiService =
        RetrofitClient.reviewApiService
) {

    suspend fun createReview(
        token: String,
        orderId: Int,
        rating: Int,
        comment: String
    ): Response<
            ApiResponse<ReviewResponse>
            > {
        return reviewApiService.createReview(
            authorization = "Bearer $token",

            request =
                CreateReviewRequest(
                    orderId = orderId,
                    rating = rating,
                    comment = comment
                )
        )
    }

    suspend fun getMyReviews(
        token: String
    ): Response<
            ApiResponse<List<ReviewResponse>>
            > {
        return reviewApiService.getMyReviews(
            authorization = "Bearer $token"
        )
    }

    suspend fun getProducerReviews(
        producerProfileId: Int
    ): Response<
            ApiResponse<List<ReviewResponse>>
            > {
        return reviewApiService
            .getProducerReviews(
                producerProfileId =
                    producerProfileId
            )
    }

    suspend fun deleteReview(
        token: String,
        reviewId: Int
    ): Response<
            ApiResponse<DeleteReviewResponse>
            > {
        return reviewApiService.deleteReview(
            authorization = "Bearer $token",
            reviewId = reviewId
        )
    }
}