package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.AddressDeleteResponse
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CreateAddressRequest
import com.homemadefood.app.data.model.UpdateAddressRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Address")
    suspend fun getAddresses():
            Response<
                    ApiResponse<
                            List<AddressResponse>
                            >
                    >

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Address")
    suspend fun createAddress(
        @Body
        request: CreateAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Address/{id}")
    suspend fun updateAddress(
        @Path("id")
        addressId: Int,

        @Body
        request: UpdateAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @DELETE("api/Address/{id}")
    suspend fun deleteAddress(
        @Path("id")
        addressId: Int
    ): Response<ApiResponse<AddressDeleteResponse>>
}