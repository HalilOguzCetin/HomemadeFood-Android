package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse
import com.homemadefood.app.data.model.NearbyProducerStorefrontResponse
import com.homemadefood.app.data.model.PagedResultResponse
import com.homemadefood.app.data.model.PopularProducerStorefrontResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StorefrontApiService {

    @GET("api/Producer/storefronts")
    suspend fun getStorefronts(
        @Query("categoryId")
        categoryId: Int? = null
    ): Response<
            ApiResponse<
                    List<ProducerStorefrontSummaryResponse>
                    >
            >

    @GET("api/Producer/storefronts/popular")
    suspend fun getPopularStorefronts(
        @Query("limit")
        limit: Int = 6
    ): Response<
            ApiResponse<
                    List<PopularProducerStorefrontResponse>
                    >
            >

    @GET("api/Producer/storefronts/nearby")
    suspend fun getNearbyStorefronts(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("radiusKm")
        radiusKm: Double = 15.0,

        @Query("limit")
        limit: Int = 6
    ): Response<
            ApiResponse<
                    List<NearbyProducerStorefrontResponse>
                    >
            >

    /*
     * H8E-2
     *
     * Home -> "Şehrimde" sekmesi.
     * Aynı şehirdeki işletmeleri backend sıralamasıyla getirir.
     */
    @GET("api/Producer/storefronts/city")
    suspend fun getCityStorefronts(
        @Query("city")
        city: String,

        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("limit")
        limit: Int = 8
    ): Response<
            ApiResponse<
                    List<DiscoverProducerStorefrontResponse>
                    >
            >

    /*
     * H8D-2A
     *
     * Keşfet ekranı için yerel + sayfalı işletme endpoint'i.
     */
    @GET("api/Producer/storefronts/discover")
    suspend fun getDiscoverStorefronts(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("city")
        city: String,

        @Query("radiusKm")
        radiusKm: Double = 30.0,

        @Query("page")
        page: Int = 1,

        @Query("pageSize")
        pageSize: Int = 20,

        @Query("categoryId")
        categoryId: Int? = null,

        @Query("search")
        search: String? = null
    ): Response<
            ApiResponse<
                    PagedResultResponse<
                            DiscoverProducerStorefrontResponse
                            >
                    >
            >

    @GET(
        "api/Producer/storefronts/{producerProfileId}/menu"
    )
    suspend fun getStorefrontMenu(
        @Path("producerProfileId")
        producerProfileId: Int
    ): Response<
            ApiResponse<
                    ProducerStorefrontMenuResponse
                    >
            >
}