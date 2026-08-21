package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse
import com.homemadefood.app.data.model.NearbyProducerStorefrontResponse
import com.homemadefood.app.data.model.PagedResultResponse
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

    suspend fun getNearbyStorefronts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 15.0,
        limit: Int = 6
    ): Response<
            ApiResponse<
                    List<NearbyProducerStorefrontResponse>
                    >
            > {
        return storefrontApiService
            .getNearbyStorefronts(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm,
                limit = limit
            )
    }

    /*
     * H8E-2
     *
     * Home -> "Şehrimde" sekmesi.
     */
    suspend fun getCityStorefronts(
        city: String,
        latitude: Double,
        longitude: Double,
        limit: Int = 8
    ): Response<
            ApiResponse<
                    List<DiscoverProducerStorefrontResponse>
                    >
            > {
        return storefrontApiService
            .getCityStorefronts(
                city = city,
                latitude = latitude,
                longitude = longitude,
                limit = limit
            )
    }

    /*
     * H8D-2A
     */
    suspend fun getDiscoverStorefronts(
        latitude: Double,
        longitude: Double,
        city: String,
        radiusKm: Double = 30.0,
        page: Int = 1,
        pageSize: Int = 20,
        categoryId: Int? = null,
        search: String? = null
    ): Response<
            ApiResponse<
                    PagedResultResponse<
                            DiscoverProducerStorefrontResponse
                            >
                    >
            > {
        return storefrontApiService
            .getDiscoverStorefronts(
                latitude = latitude,
                longitude = longitude,
                city = city,
                radiusKm = radiusKm,
                page = page,
                pageSize = pageSize,
                categoryId = categoryId,
                search = search
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