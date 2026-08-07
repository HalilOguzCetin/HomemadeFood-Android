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
        RetrofitClient
            .favoriteApiService
) {

    /*
     * token parametresi geçiş aşamasında
     * eski ViewModel çağrılarını bozmamak
     * amacıyla geçici olarak tutuluyor.
     *
     * JWT artık repository tarafından
     * API'ye gönderilmiyor.
     */
    suspend fun getFavorites():
            Response<ApiResponse<List<FavoriteResponse>>> {

        return favoriteApiService
            .getFavorites()
    }

    suspend fun addFavorite(
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>> {

        return favoriteApiService.addFavorite(
            foodId = foodId
        )
    }

    suspend fun removeFavorite(
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>> {

        return favoriteApiService
            .removeFavorite(
                foodId = foodId
            )
    }
}