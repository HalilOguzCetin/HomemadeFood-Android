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
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @GET("api/Cart")
    suspend fun getCart():
            Response<ApiResponse<CartResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @POST("api/Cart/items")
    suspend fun addItem(
        @Body
        request: AddCartItemRequest
    ): Response<ApiResponse<CartResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @PUT("api/Cart/items/{cartItemId}")
    suspend fun updateItem(
        @Path("cartItemId")
        cartItemId: Int,

        @Body
        request: UpdateCartItemRequest
    ): Response<ApiResponse<CartResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @DELETE("api/Cart/items/{cartItemId}")
    suspend fun removeItem(
        @Path("cartItemId")
        cartItemId: Int
    ): Response<ApiResponse<CartResponse>>

    @Headers(
        "X-HomemadeFood-Requires-Auth: true"
    )
    @DELETE("api/Cart")
    suspend fun clearCart():
            Response<ApiResponse<ClearCartResponse>>
}