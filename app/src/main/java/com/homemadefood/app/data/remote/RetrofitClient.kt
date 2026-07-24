package com.homemadefood.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "http://127.0.0.1:5062/"

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level =
                HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(
            AuthApiService::class.java
        )
    }
    val categoryApiService:
            CategoryApiService by lazy {

        retrofit.create(
            CategoryApiService::class.java
        )
    }
    val foodApiService:
            FoodApiService by lazy {

        retrofit.create(
            FoodApiService::class.java
        )
    }
    val favoriteApiService:
            FavoriteApiService by lazy {

        retrofit.create(
            FavoriteApiService::class.java
        )
    }
}