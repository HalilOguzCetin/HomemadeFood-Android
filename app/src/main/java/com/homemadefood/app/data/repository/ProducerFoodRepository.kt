package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateFoodRequest
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.UpdateFoodRequest
import com.homemadefood.app.data.remote.ProducerFoodApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class ProducerFoodRepository(
    private val producerFoodApiService:
    ProducerFoodApiService =
        RetrofitClient.producerFoodApiService
) {

    suspend fun getMyFoods(
        token: String
    ): Response<
            ApiResponse<
                    List<FoodResponse>
                    >
            > {
        return producerFoodApiService.getMyFoods(
            authorization = "Bearer $token"
        )
    }
    suspend fun getFoodById(
        token: String,
        foodId: Int
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            > {
        return producerFoodApiService.getFoodById(
            authorization = "Bearer $token",
            foodId = foodId
        )
    }

    suspend fun createFood(
        token: String,
        request: CreateFoodRequest
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            > {
        return producerFoodApiService.createFood(
            authorization = "Bearer $token",
            request = request
        )
    }

    suspend fun updateFood(
        token: String,
        foodId: Int,
        request: UpdateFoodRequest
    ): Response<
            ApiResponse<
                    FoodResponse
                    >
            > {
        return producerFoodApiService.updateFood(
            authorization = "Bearer $token",
            foodId = foodId,
            request = request
        )
    }
}