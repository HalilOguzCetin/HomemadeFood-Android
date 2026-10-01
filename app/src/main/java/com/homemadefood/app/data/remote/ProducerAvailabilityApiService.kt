package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerAvailabilityResponse
import com.homemadefood.app.data.model.UpdateProducerAvailabilityModeRequest
import com.homemadefood.app.data.model.UpdateProducerBusinessHoursRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT

interface ProducerAvailabilityApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Producer/my-availability")
    suspend fun getMyAvailability():
            Response<
                    ApiResponse<
                            ProducerAvailabilityResponse
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Producer/my-business-hours")
    suspend fun updateMyBusinessHours(
        @Body
        request:
        UpdateProducerBusinessHoursRequest
    ): Response<
            ApiResponse<
                    ProducerAvailabilityResponse
                    >
            >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Producer/my-availability-mode")
    suspend fun updateMyAvailabilityMode(
        @Body
        request:
        UpdateProducerAvailabilityModeRequest
    ): Response<
            ApiResponse<
                    ProducerAvailabilityResponse
                    >
            >
}