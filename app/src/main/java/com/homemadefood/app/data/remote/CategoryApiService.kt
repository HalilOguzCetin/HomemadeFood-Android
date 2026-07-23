package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApiService {

    @GET("api/Category")
    suspend fun getCategories():
            Response<ApiResponse<List<CategoryResponse>>>
}