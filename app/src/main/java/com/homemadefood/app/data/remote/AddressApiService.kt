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
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApiService {

    @GET("api/Address")
    suspend fun getAddresses(
        @Header("Authorization")
        authorization: String
    ): Response<ApiResponse<List<AddressResponse>>>

    @POST("api/Address")
    suspend fun createAddress(
        @Header("Authorization")
        authorization: String,

        @Body
        request: CreateAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @PUT("api/Address/{id}")
    suspend fun updateAddress(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        addressId: Int,

        @Body
        request: UpdateAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @DELETE("api/Address/{id}")
    suspend fun deleteAddress(
        @Header("Authorization")
        authorization: String,

        @Path("id")
        addressId: Int
    ): Response<ApiResponse<AddressDeleteResponse>>
}