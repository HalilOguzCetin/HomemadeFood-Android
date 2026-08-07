package com.homemadefood.app.data.remote

import com.homemadefood.app.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    companion object {

        const val AUTH_MARKER_HEADER =
            "X-HomemadeFood-Requires-Auth"

        const val AUTH_MARKER_VALUE =
            "true"
    }

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val originalRequest =
            chain.request()

        /*
         * Yalnızca açık şekilde kimlik doğrulama
         * isteyen endpointlere JWT eklenir.
         *
         * Böylece login/register gibi public
         * endpointlere yanlışlıkla token gitmez.
         */
        val requiresAuthentication =
            originalRequest.header(
                AUTH_MARKER_HEADER
            ) == AUTH_MARKER_VALUE

        if (!requiresAuthentication) {
            return chain.proceed(
                originalRequest
            )
        }

        /*
         * Bu işaret yalnızca uygulama içinde
         * kullanılır. Sunucuya gönderilmez.
         */
        val requestBuilder =
            originalRequest
                .newBuilder()
                .removeHeader(
                    AUTH_MARKER_HEADER
                )

        val token =
            runBlocking {
                sessionManager
                    .token
                    .first()
            }

        if (!token.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $token"
            )
        }

        return chain.proceed(
            requestBuilder.build()
        )
    }
}