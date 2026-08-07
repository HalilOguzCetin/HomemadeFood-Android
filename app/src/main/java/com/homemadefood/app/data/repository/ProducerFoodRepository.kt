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

    suspend fun getMyFoods():
            Response<
                    ApiResponse<List<FoodResponse>>
                    > {

        return producerFoodApiService
            .getMyFoods()
    }

    suspend fun getFoodById(
        foodId: Int
    ): Response<
            ApiResponse<FoodResponse>
            > {

        return producerFoodApiService
            .getFoodById(
                foodId = foodId
            )
    }

    suspend fun createFood(
        request: CreateFoodRequest
    ): Response<
            ApiResponse<FoodResponse>
            > {

        return producerFoodApiService
            .createFood(
                request = request
            )
    }

    suspend fun updateFood(
        foodId: Int,
        request: UpdateFoodRequest
    ): Response<
            ApiResponse<FoodResponse>
            > {

        return producerFoodApiService
            .updateFood(
                foodId = foodId,
                request = request
            )
    }
}