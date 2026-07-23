package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.remote.FoodApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class FoodRepository(
    private val foodApiService: FoodApiService =
        RetrofitClient.foodApiService
) {

    suspend fun getFoods(
        categoryId: Int? = null,
        search: String? = null
    ): Response<ApiResponse<List<FoodResponse>>> {

        return foodApiService.getFoods(
            categoryId = categoryId,
            search = search
        )
    }

    suspend fun getFoodById(
        foodId: Int
    ): Response<ApiResponse<FoodResponse>> {

        return foodApiService.getFoodById(
            foodId = foodId
        )
    }
}