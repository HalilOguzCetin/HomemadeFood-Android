package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationRequest
import com.homemadefood.app.data.model.ProducerApplicationStatusResponse
import com.homemadefood.app.data.model.ProducerApplicationSubmitResponse
import com.homemadefood.app.data.model.UpdateProducerProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProducerApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Producer/apply")
    suspend fun apply(
        @Body
        request: ProducerApplicationRequest
    ): Response<
            ApiResponse<
                    ProducerApplicationSubmitResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Producer/my-application")
    suspend fun getMyApplication():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Producer/my-profile")
    suspend fun getMyProfile():
            Response<
                    ApiResponse<
                            ProducerApplicationStatusResponse
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Producer/my-profile")
    suspend fun updateMyProfile(
        @Body
        request: UpdateProducerProfileRequest
    ): Response<
            ApiResponse<
                    ProducerApplicationStatusResponse
                    >
            >
}