package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateReviewRequest
import com.homemadefood.app.data.model.DeleteReviewResponse
import com.homemadefood.app.data.model.ReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @POST("api/Review")
    suspend fun createReview(
        @Header("Authorization")
        authorization: String,

        @Body
        request: CreateReviewRequest
    ): Response<
            ApiResponse<ReviewResponse>
            >

    @GET("api/Review/my-reviews")
    suspend fun getMyReviews(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<List<ReviewResponse>>
            >

    @GET("api/Review/producer/{producerProfileId}")
    suspend fun getProducerReviews(
        @Path("producerProfileId")
        producerProfileId: Int
    ): Response<
            ApiResponse<List<ReviewResponse>>
            >

    @DELETE("api/Review/{id}")
    suspend fun deleteReview(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        reviewId: Int
    ): Response<
            ApiResponse<DeleteReviewResponse>
            >
}