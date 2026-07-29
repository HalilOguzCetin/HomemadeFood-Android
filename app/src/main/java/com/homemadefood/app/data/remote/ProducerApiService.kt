package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProducerApiService {

    @GET("api/Producer/my-application")
    suspend fun getMyApplication(
        @Header("Authorization")
        authorization: String
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            >
}