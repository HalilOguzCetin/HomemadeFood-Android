package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.remote.CategoryApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

class CategoryRepository(
    private val categoryApiService: CategoryApiService =
        RetrofitClient.categoryApiService
) {

    suspend fun getCategories():
            Response<ApiResponse<List<CategoryResponse>>> {

        return categoryApiService.getCategories()
    }
}