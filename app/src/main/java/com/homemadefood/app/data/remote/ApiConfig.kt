package com.homemadefood.app.data.remote

object ApiConfig {
    const val BASE_URL =
        "http://127.0.0.1:5062/"

    fun resolveMediaUrl(
        imageUrl: String?
    ): String? {
        val value =
            imageUrl
                ?.trim()
                .orEmpty()

        if (value.isBlank()) {
            return null
        }

        if (
            value.startsWith(
                "http://",
                ignoreCase = true
            ) ||
            value.startsWith(
                "https://",
                ignoreCase = true
            )
        ) {
            return value
        }

        return BASE_URL.trimEnd('/') +
                "/" +
                value.trimStart('/')
    }
}