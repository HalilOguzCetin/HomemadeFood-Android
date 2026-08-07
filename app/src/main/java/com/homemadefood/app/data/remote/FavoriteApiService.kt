package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.FavoriteResponse
import com.homemadefood.app.data.model.FoodActionResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Favorite")
    suspend fun getFavorites():
            Response<
                    ApiResponse<
                            List<FavoriteResponse>
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Favorite/{foodId}")
    suspend fun addFavorite(
        @Path("foodId")
        foodId: Int
    ): Response<
            ApiResponse<
                    FoodActionResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @DELETE("api/Favorite/{foodId}")
    suspend fun removeFavorite(
        @Path("foodId")
        foodId: Int
    ): Response<
            ApiResponse<
                    FoodActionResponse
                    >
            >
}