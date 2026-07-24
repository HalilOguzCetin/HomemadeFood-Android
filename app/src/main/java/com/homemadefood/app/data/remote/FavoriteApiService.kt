package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FavoriteResponse
import com.homemadefood.app.data.model.FoodActionResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {

    @GET("api/Favorite")
    suspend fun getFavorites(
        @Header("Authorization")
        authorization: String
    ): Response<ApiResponse<List<FavoriteResponse>>>

    @POST("api/Favorite/{foodId}")
    suspend fun addFavorite(
        @Header("Authorization")
        authorization: String,

        @Path("foodId")
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>>

    @DELETE("api/Favorite/{foodId}")
    suspend fun removeFavorite(
        @Header("Authorization")
        authorization: String,

        @Path("foodId")
        foodId: Int
    ): Response<ApiResponse<FoodActionResponse>>
}