package com.homemadefood.app.data.remote

import com.homemadefood.app.data.model.AddCartItemRequest
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.CartResponse
import com.homemadefood.app.data.model.ClearCartResponse
import com.homemadefood.app.data.model.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {

    @GET("api/Cart")
    suspend fun getCart(
        @Header("Authorization")
        authorization: String
    ): Response<ApiResponse<CartResponse>>

    @POST("api/Cart/items")
    suspend fun addItem(
        @Header("Authorization")
        authorization: String,

        @Body
        request: AddCartItemRequest
    ): Response<ApiResponse<CartResponse>>

    @PUT("api/Cart/items/{cartItemId}")
    suspend fun updateItem(
        @Header("Authorization")
        authorization: String,

        @Path("cartItemId")
        cartItemId: Int,

        @Body
        request: UpdateCartItemRequest
    ): Response<ApiResponse<CartResponse>>

    @DELETE("api/Cart/items/{cartItemId}")
    suspend fun removeItem(
        @Header("Authorization")
        authorization: String,

        @Path("cartItemId")
        cartItemId: Int
    ): Response<ApiResponse<CartResponse>>

    @DELETE("api/Cart")
    suspend fun clearCart(
        @Header("Authorization")
        authorization: String
    ): Response<ApiResponse<ClearCartResponse>>
}