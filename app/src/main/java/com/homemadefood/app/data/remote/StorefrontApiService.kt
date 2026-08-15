package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuResponse
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StorefrontApiService {

    /*
     * Public/customer-facing işletme vitrini endpoint'i.
     * Auth marker eklenmez; backend endpoint'i AllowAnonymous'dır.
     */
    @GET("api/Producer/storefronts")
    suspend fun getStorefronts(
        @Query("categoryId")
        categoryId: Int? = null
    ): Response<
            ApiResponse<
                    List<ProducerStorefrontSummaryResponse>
                    >
            >

    /*
     * Seçilen işletmenin müşteriye açık menüsünü getirir.
     * Backend response'u yemekleri kategori bazında gruplanmış
     * şekilde döndürür.
     */
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