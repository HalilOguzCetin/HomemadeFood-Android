package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ProducerApiService {

    @POST("api/Producer/apply")
    suspend fun apply(
        @Header("Authorization")
        authorization: String,

        @Body
        request: ProducerApplicationRequest
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            >

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