package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.UpdateFoodAvailabilityRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProducerFoodApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Food/my-foods")
    suspend fun getMyFoods():
            Response<
                    ApiResponse<List<FoodResponse>>
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Food/my-foods/{id}")
    suspend fun getFoodById(
        @Path("id")
        foodId: Int
    ): Response<
            ApiResponse<FoodResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @Multipart
    @POST("api/Food")
    suspend fun createFood(
        @Part("CategoryId")
        categoryId: RequestBody,

        @Part("Name")
        name: RequestBody,

        @Part("Description")
        description: RequestBody,

        @Part("Ingredients")
        ingredients: RequestBody,

        @Part("Price")
        price: RequestBody,

        @Part("UnitType")
        unitType: RequestBody,

        @Part("DailyCapacity")
        dailyCapacity: RequestBody,

        @Part("PreparationTimeMinutes")
        preparationTimeMinutes: RequestBody,

        @Part("MinimumOrderLeadTimeMinutes")
        minimumOrderLeadTimeMinutes: RequestBody,

        @Part
        allergenCodes: List<MultipartBody.Part>,

        @Part
        image: MultipartBody.Part
    ): Response<
            ApiResponse<FoodResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @Multipart
    @PUT("api/Food/{id}")
    suspend fun updateFood(
        @Path("id")
        foodId: Int,

        @Part("CategoryId")
        categoryId: RequestBody,

        @Part("Name")
        name: RequestBody,

        @Part("Description")
        description: RequestBody,

        @Part("Ingredients")
        ingredients: RequestBody,

        @Part("Price")
        price: RequestBody,

        @Part("UnitType")
        unitType: RequestBody,

        @Part("DailyCapacity")
        dailyCapacity: RequestBody,

        @Part("PreparationTimeMinutes")
        preparationTimeMinutes: RequestBody,

        @Part("MinimumOrderLeadTimeMinutes")
        minimumOrderLeadTimeMinutes: RequestBody,

        @Part
        allergenCodes: List<MultipartBody.Part>,

        @Part("IsAvailable")
        isAvailable: RequestBody,

        @Part
        image: MultipartBody.Part?
    ): Response<
            ApiResponse<FoodResponse>
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Food/my-foods/{id}/availability")
    suspend fun updateFoodAvailability(
        @Path("id")
        foodId: Int,
        @Body
        request: UpdateFoodAvailabilityRequest
    ): Response<
            ApiResponse<FoodResponse>
            >
}
