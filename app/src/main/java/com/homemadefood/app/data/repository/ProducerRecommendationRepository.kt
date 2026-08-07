package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerRecommendationRequest
import com.homemadefood.app.data.model.ProducerRecommendationResultResponse
import com.homemadefood.app.data.model.ProducerRecommendationSelectionRequest
import com.homemadefood.app.data.model.ProducerRecommendationSelectionResponse
import com.homemadefood.app.data.remote.ProducerRecommendationApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerRecommendationRepository(
    private val apiService:
    ProducerRecommendationApiService =
        RetrofitClient
            .producerRecommendationApiService
) {

    suspend fun getRecommendations(
        searchText: String,
        addressId: Int,
        quantity: Int?
    ): Response<
            ApiResponse<
                    ProducerRecommendationResultResponse
                    >
            > {

        val request =
            ProducerRecommendationRequest(
                searchText =
                    searchText.trim(),

                addressId =
                    addressId,

                quantity =
                    quantity
            )

        return apiService
            .getRecommendations(
                request = request
            )
    }

    suspend fun selectRecommendation(
        recommendationSearchId: Int,
        foodId: Int
    ): Response<
            ApiResponse<
                    ProducerRecommendationSelectionResponse
                    >
            > {

        val request =
            ProducerRecommendationSelectionRequest(
                recommendationSearchId =
                    recommendationSearchId,

                foodId =
                    foodId
            )

        return apiService
            .selectRecommendation(
                request = request
            )
    }
}