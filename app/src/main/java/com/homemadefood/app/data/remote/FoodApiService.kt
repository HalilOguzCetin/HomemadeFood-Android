package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FoodResponse
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
    ): Response<ApiResponse<List<FoodResponse>>>

    @GET("api/Food/{id}")
    suspend fun getFoodById(
        @Path("id")
        foodId: Int
    ): Response<ApiResponse<FoodResponse>>
}