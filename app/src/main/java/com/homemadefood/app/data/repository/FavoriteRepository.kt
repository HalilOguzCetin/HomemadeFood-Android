package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FavoriteResponse
import com.homemadefood.app.data.model.FoodActionResponse
import com.homemadefood.app.data.remote.FavoriteApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class FavoriteRepository(
    private val favoriteApiService:
    FavoriteApiService =
        RetrofitClient.favoriteApiService
) {

    suspend fun getFavorites(
        token: String
    ): Response<ApiResponse<List<FavoriteResponse>>> {

        return favoriteApiService.getFavorites(
            authorization = "Bearer $token"
        )
    }

    suspend fun addFavorite(
        token: String,
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>> {

        return favoriteApiService.addFavorite(
            authorization = "Bearer $token",
            foodId = foodId
        )
    }

    suspend fun removeFavorite(
        token: String,
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>> {

        return favoriteApiService.removeFavorite(
            authorization = "Bearer $token",
            foodId = foodId
        )
    }
}