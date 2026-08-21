package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.DiscoverFoodResponse
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.model.PagedResultResponse
import com.homemadefood.app.data.model.PopularFoodResponse
import com.homemadefood.app.data.remote.FoodApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class FoodRepository(
    private val foodApiService:
    FoodApiService =
        RetrofitClient.foodApiService
) {

    suspend fun getFoods(
        categoryId: Int? = null,
        search: String? = null
    ): Response<
            ApiResponse<
                    List<FoodResponse>
                    >
            > {
        return foodApiService
            .getFoods(
                categoryId = categoryId,
                search = search
            )
    }

    suspend fun getPopularFoods(
        limit: Int = 8
    ): Response<
            ApiResponse<
                    List<PopularFoodResponse>
                    >
            > {
        return foodApiService
            .getPopularFoods(
                limit = limit
            )
    }

    /*
     * H8D-2A
     */
    suspend fun getDiscoverFoods(
        latitude: Double,
        longitude: Double,
        city: String,
        radiusKm: Double = 30.0,
        page: Int = 1,
        pageSize: Int = 20,
        categoryId: Int? = null,
        search: String? = null
    ): Response<
            ApiResponse<
                    PagedResultResponse<
                            DiscoverFoodResponse
                            >
                    >
            > {
        return foodApiService
            .getDiscoverFoods(
                latitude = latitude,
                longitude = longitude,
                city = city,
                radiusKm = radiusKm,
                page = page,
                pageSize = pageSize,
                categoryId = categoryId,
                search = search
            )
    }

    suspend fun getFoodById(
        foodId: Int
    ): Response<
            ApiResponse<FoodResponse>
            > {
        return foodApiService
            .getFoodById(
                foodId = foodId
            )
    }
}