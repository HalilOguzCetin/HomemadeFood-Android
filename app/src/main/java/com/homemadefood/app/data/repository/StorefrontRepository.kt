package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.PopularProducerStorefrontResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.remote.RetrofitClient
import com.homemadefood.app.data.remote.StorefrontApiService
import retrofit2.Response

class StorefrontRepository(
    private val storefrontApiService:
    StorefrontApiService =
        RetrofitClient.storefrontApiService
) {

    suspend fun getStorefronts(
        categoryId: Int? = null
    ): Response<
            ApiResponse<
                    List<ProducerStorefrontSummaryResponse>
                    >
            > {
        return storefrontApiService
            .getStorefronts(
                categoryId = categoryId
            )
    }

    suspend fun getPopularStorefronts(
        limit: Int = 6
    ): Response<
            ApiResponse<
                    List<PopularProducerStorefrontResponse>
                    >
            > {
        return storefrontApiService
            .getPopularStorefronts(
                limit = limit
            )
    }

    suspend fun getStorefrontMenu(
        producerProfileId: Int
    ): Response<
            ApiResponse<
                    ProducerStorefrontMenuResponse
                    >
            > {
        return storefrontApiService
            .getStorefrontMenu(
                producerProfileId =
                    producerProfileId
            )
    }
}