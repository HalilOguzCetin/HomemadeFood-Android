package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.UpdateFoodAvailabilityRequest
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
        ingredients: String,
        price: Double,
        preparationTimeMinutes: Int,
        image: MultipartBody.Part,
        unitType: String = "Portion",
        dailyCapacity: Int = 1,
        minimumOrderLeadTimeMinutes: Int = 0,
        allergenCodes: List<String> = emptyList()
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

                ingredients =
                    ingredients.toRequestBody(
                        plainTextMediaType
                    ),

                price =
                    normalizedPrice
                        .toRequestBody(
                            plainTextMediaType
                        ),

                unitType =
                    unitType.toRequestBody(
                        plainTextMediaType
                    ),

                dailyCapacity =
                    dailyCapacity
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                preparationTimeMinutes =
                    preparationTimeMinutes
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                minimumOrderLeadTimeMinutes =
                    minimumOrderLeadTimeMinutes
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                allergenCodes =
                    createAllergenParts(
                        allergenCodes
                    ),

                image = image
            )
    }

    suspend fun updateFood(
        foodId: Int,
        categoryId: Int,
        name: String,
        description: String,
        ingredients: String,
        price: Double,
        preparationTimeMinutes: Int,
        isAvailable: Boolean,
        image: MultipartBody.Part?,
        unitType: String = "Portion",
        dailyCapacity: Int = 1,
        minimumOrderLeadTimeMinutes: Int = 0,
        allergenCodes: List<String> = emptyList()
    ): Response<
            ApiResponse<FoodResponse>
            > {

        val normalizedPrice =
            BigDecimal.valueOf(price)
                .stripTrailingZeros()
                .toPlainString()

        return producerFoodApiService
            .updateFood(
                foodId = foodId,

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

                ingredients =
                    ingredients.toRequestBody(
                        plainTextMediaType
                    ),

                price =
                    normalizedPrice
                        .toRequestBody(
                            plainTextMediaType
                        ),

                unitType =
                    unitType.toRequestBody(
                        plainTextMediaType
                    ),

                dailyCapacity =
                    dailyCapacity
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                preparationTimeMinutes =
                    preparationTimeMinutes
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                minimumOrderLeadTimeMinutes =
                    minimumOrderLeadTimeMinutes
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                allergenCodes =
                    createAllergenParts(
                        allergenCodes
                    ),

                isAvailable =
                    isAvailable
                        .toString()
                        .toRequestBody(
                            plainTextMediaType
                        ),

                image = image
            )
    }

    suspend fun updateFoodAvailability(
        foodId: Int,
        isAvailable: Boolean
    ): Response<
            ApiResponse<FoodResponse>
            > {

        return producerFoodApiService
            .updateFoodAvailability(
                foodId = foodId,
                request =
                    UpdateFoodAvailabilityRequest(
                        isAvailable = isAvailable
                    )
            )
    }

    private fun createAllergenParts(
        allergenCodes: List<String>
    ): List<MultipartBody.Part> {
        return allergenCodes
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .map { code ->
                MultipartBody.Part.createFormData(
                    "AllergenCodes",
                    code
                )
            }
    }
}
