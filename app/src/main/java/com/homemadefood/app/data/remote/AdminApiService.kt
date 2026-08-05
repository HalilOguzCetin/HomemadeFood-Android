package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.RecommendationPerformanceResponse
import com.homemadefood.app.data.model.RejectProducerApplicationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {

    @GET("api/Admin/producer-applications")
    suspend fun getProducerApplications(
        @Header("Authorization")
        authorization: String,

        @Query("status")
        status: String
    ): Response<
            ApiResponse<
                    List<AdminProducerApplicationResponse>
                    >
            >

    @POST(
        "api/Admin/producer-applications/{id}/approve"
    )
    suspend fun approveProducerApplication(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        producerProfileId: Int
    ): Response<ApiResponse<Any?>>

    @POST(
        "api/Admin/producer-applications/{id}/reject"
    )
    suspend fun rejectProducerApplication(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        producerProfileId: Int,

        @Body
        request: RejectProducerApplicationRequest
    ): Response<ApiResponse<Any?>>

    @GET("api/RecommendationAnalytics/summary")
    suspend fun getRecommendationAnalytics(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<
                    RecommendationPerformanceResponse
                    >
            >
}