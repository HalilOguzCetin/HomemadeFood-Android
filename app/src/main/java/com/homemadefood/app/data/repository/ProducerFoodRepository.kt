package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.UpdateFoodRequest
import com.homemadefood.app.data.remote.ProducerFoodApiService
import com.homemadefood.app.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.math.BigDecimal

class ProducerFoodRepository(
    private val producerFoodApiService:
    ProducerFoodApiService =
        RetrofitClient.producerFoodApiService
) {

    private val plainTextMediaType =
        "text/plain".toMediaType()

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
        categoryId: Int,
        name: String,
        description: String,
        price: Double,
        preparationTimeMinutes: Int,
        image: MultipartBody.Part
    ): Response<
            ApiResponse<FoodResponse>
            > {

        val normalizedPrice =
            BigDecimal.valueOf(price)
                .stripTrailingZeros()
                .toPlainString()

        return producerFoodApiService
            .createFood(
                categoryId =
                    categoryId.toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                name =
                    name.toRequestBody(
                        plainTextMediaType
                    ),

                description =
                    description.toRequestBody(
                        plainTextMediaType
                    ),

                price =
                    normalizedPrice
                        .toRequestBody(
                            plainTextMediaType
                        ),

                preparationTimeMinutes =
                    preparationTimeMinutes
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                image = image
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