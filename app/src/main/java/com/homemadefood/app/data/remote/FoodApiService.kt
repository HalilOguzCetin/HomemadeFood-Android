package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.DiscoverFoodResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.PagedResultResponse
import com.homemadefood.app.data.model.PopularFoodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {

    @GET("api/Food")
    suspend fun getFoods(
        @Query("categoryId")
        categoryId: Int? = null,

        @Query("search")
        search: String? = null
    ): Response<
            ApiResponse<
                    List<FoodResponse>
                    >
            >

    @GET("api/Food/popular")
    suspend fun getPopularFoods(
        @Query("limit")
        limit: Int = 8
    ): Response<
            ApiResponse<
                    List<PopularFoodResponse>
                    >
            >

    /*
     * H8D-2A
     *
     * Keşfet ekranı için yerel + sayfalı yemek endpoint'i.
     * Producer koordinatları response'a gelmez.
     */
    @GET("api/Food/discover")
    suspend fun getDiscoverFoods(
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
                            DiscoverFoodResponse
                            >
                    >
            >

    @GET("api/Food/{id}")
    suspend fun getFoodById(
        @Path("id")
        foodId: Int
    ): Response<
            ApiResponse<FoodResponse>
            >
}