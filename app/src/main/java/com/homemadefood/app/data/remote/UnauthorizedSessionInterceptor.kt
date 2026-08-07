package com.homemadefood.app.data.remote

import com.homemadefood.app.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean

class UnauthorizedSessionInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    private val isClearingSession =
        AtomicBoolean(false)

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val response =
            chain.proceed(
                chain.request()
            )

        /*
         * response.request kullanıyoruz.
         *
         * Böylece Authorization header ileride
         * başka bir interceptor tarafından eklenirse
         * yine doğru biçimde algılanır.
         */
        val hasAuthorizationHeader =
            !response.request
                .header("Authorization")
                .isNullOrBlank()

        val shouldClearSession =
            response.code == 401 &&
                    hasAuthorizationHeader

        if (
            shouldClearSession &&
            isClearingSession.compareAndSet(
                false,
                true
            )
        ) {
            try {

                /*
                 * 401 cevabı uygulamanın geri kalanına
                 * ulaşmadan önce oturum tamamen silinir.
                 *
                 * OkHttp interceptor zaten arka plandaki
                 * network thread üzerinde çalışır.
                 */
                runBlocking {
                    sessionManager
                        .clearSession()
                }

            } finally {
                isClearingSession.set(
                    false
                )
            }
        }

        return response
    }
}