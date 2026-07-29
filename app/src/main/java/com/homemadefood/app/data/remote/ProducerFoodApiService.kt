package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateFoodRequest
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.UpdateFoodRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProducerFoodApiService {

    @GET("api/Food/my-foods")
    suspend fun getMyFoods(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<
                    List<FoodResponse>
                    >
            >
    @GET("api/Food/my-foods/{id}")
    suspend fun getFoodById(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        foodId: Int
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            >

    @POST("api/Food")
    suspend fun createFood(
        @Header("Authorization")
        authorization: String,

        @Body
        request: CreateFoodRequest
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            >

    @PUT("api/Food/{id}")
    suspend fun updateFood(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        foodId: Int,

        @Body
        request: UpdateFoodRequest
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            >
}