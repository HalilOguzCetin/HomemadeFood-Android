package com.homemadefood.app.data.remote

import com.homemadefood.app.data.local.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean

class UnauthorizedSessionInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    private val coroutineScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    private val isClearingSession =
        AtomicBoolean(false)

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val request =
            chain.request()

        val response =
            chain.proceed(request)

        val hasAuthorizationHeader =
            !request.header("Authorization")
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
            coroutineScope.launch {
                try {
                    sessionManager.clearSession()
                } finally {
                    isClearingSession.set(false)
                }
            }
        }

        return response
    }
}