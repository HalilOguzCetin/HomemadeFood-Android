package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerRecommendationRequest
import com.homemadefood.app.data.model.ProducerRecommendationResultResponse
import com.homemadefood.app.data.model.ProducerRecommendationSelectionRequest
import com.homemadefood.app.data.model.ProducerRecommendationSelectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProducerRecommendationApiService {

    @POST(
        "api/ProducerRecommendation/recommend"
    )
    suspend fun getRecommendations(
        @Header("Authorization")
        authorization: String,

        @Body
        request: ProducerRecommendationRequest
    ): Response<
            ApiResponse<
                    ProducerRecommendationResultResponse
                    >
            >

    @POST(
        "api/ProducerRecommendation/select"
    )
    suspend fun selectRecommendation(
        @Header("Authorization")
        authorization: String,

        @Body
        request:
        ProducerRecommendationSelectionRequest
    ): Response<
            ApiResponse<
                    ProducerRecommendationSelectionResponse
                    >
            >
}