package com.homemadefood.app.data.remote

import android.content.Context
import com.homemadefood.app.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.homemadefood.app.BuildConfig

object RetrofitClient {

    private const val BASE_URL =
        "http://127.0.0.1:5062/"

    @Volatile
    private var isInitialized: Boolean =
        false

    private lateinit var sessionManager:
            SessionManager

    fun initialize(
        context: Context
    ) {
        if (isInitialized) {
            return
        }

        synchronized(this) {
            if (!isInitialized) {
                sessionManager =
                    SessionManager(
                        context.applicationContext
                    )

                isInitialized = true
            }
        }
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {

            /*
             * BODY kullanılmaz.
             *
             * Çünkü login request body içinde şifre,
             * login response body içinde JWT bulunabilir.
             *
             * Debug ortamında yalnızca:
             * - HTTP metodu
             * - URL
             * - durum kodu
             * - süre
             *
             * gibi temel bilgiler loglanır.
             */
            level =
                HttpLoggingInterceptor.Level.BASIC

            /*
             * İleride log seviyesi değiştirilse bile
             * hassas header değerleri gizli tutulur.
             */
            redactHeader(
                "Authorization"
            )

            redactHeader(
                "Cookie"
            )

            redactHeader(
                "Set-Cookie"
            )
        }

    private val okHttpClient:
            OkHttpClient by lazy {

        check(isInitialized) {
            "RetrofitClient.initialize(context) çağrılmalıdır."
        }

        val builder =
            OkHttpClient.Builder()

                /*
                 * Önce gerekiyorsa JWT eklenir.
                 */
                .addInterceptor(
                    AuthorizationInterceptor(
                        sessionManager
                    )
                )

                /*
                 * Sonrasında 401 cevabı alınırsa
                 * güvenli session temizliği yapılır.
                 */
                .addInterceptor(
                    UnauthorizedSessionInterceptor(
                        sessionManager
                    )
                )

        /*
         * HTTP logları yalnızca debug
         * sürümünde aktiftir.
         */
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                loggingInterceptor
            )
        }

        builder.build()
    }

    private val retrofit:
            Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApiService:
            AuthApiService by lazy {

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

    val addressApiService:
            AddressApiService by lazy {

        retrofit.create(
            AddressApiService::class.java
        )
    }

    val cartApiService:
            CartApiService by lazy {

        retrofit.create(
            CartApiService::class.java
        )
    }

    val orderApiService:
            OrderApiService by lazy {

        retrofit.create(
            OrderApiService::class.java
        )
    }

    val producerRecommendationApiService:
            ProducerRecommendationApiService by lazy {

        retrofit.create(
            ProducerRecommendationApiService::class.java
        )
    }

    val producerApiService:
            ProducerApiService by lazy {

        retrofit.create(
            ProducerApiService::class.java
        )
    }

    val producerFoodApiService:
            ProducerFoodApiService by lazy {

        retrofit.create(
            ProducerFoodApiService::class.java
        )
    }

    val producerOrderApiService:
            ProducerOrderApiService by lazy {

        retrofit.create(
            ProducerOrderApiService::class.java
        )
    }

    val adminApiService:
            AdminApiService by lazy {

        retrofit.create(
            AdminApiService::class.java
        )
    }
    val reviewApiService:
            ReviewApiService by lazy {

        retrofit.create(
            ReviewApiService::class.java
        )
    }
}