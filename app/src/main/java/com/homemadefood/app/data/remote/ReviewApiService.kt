package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateReviewRequest
import com.homemadefood.app.data.model.DeleteReviewResponse
import com.homemadefood.app.data.model.ReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Review")
    suspend fun createReview(
        @Body
        request: CreateReviewRequest
    ): Response<
            ApiResponse<ReviewResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Review/my-reviews")
    suspend fun getMyReviews():
            Response<
                    ApiResponse<List<ReviewResponse>>
                    >

    /*
     * Public endpoint.
     * Üretici yorumlarını görmek için
     * kullanıcının giriş yapması gerekmez.
     */
    @GET("api/Review/producer/{producerProfileId}")
    suspend fun getProducerReviews(
        @Path("producerProfileId")
        producerProfileId: Int
    ): Response<
            ApiResponse<List<ReviewResponse>>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @DELETE("api/Review/{id}")
    suspend fun deleteReview(
        @Path("id")
        reviewId: Int
    ): Response<
            ApiResponse<DeleteReviewResponse>
            >
}