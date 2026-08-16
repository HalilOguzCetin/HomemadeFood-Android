package com.homemadefood.app.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class ApiErrorDetails(
    val code: String? = null,
    val message: String? = null
)

object ApiErrorParser {

    fun parse(
        errorJson: String?
    ): ApiErrorDetails {

        if (errorJson.isNullOrBlank()) {
            return ApiErrorDetails()
        }

        return runCatching {

            val root =
                JSONObject(errorJson)

            val code =
                firstNonBlank(
                    root.optString("code"),
                    root.optString("Code")
                )

            /*
             * HomemadeFood standart ApiResponse<T> hata mesajı
             * önceliklidir.
             */
            val standardMessage =
                firstNonBlank(
                    root.optString("message"),
                    root.optString("Message")
                )

            if (standardMessage != null) {
                return@runCatching ApiErrorDetails(
                    code = code,
                    message = standardMessage
                )
            }

            /*
             * ASP.NET Core model validation cevabı hem doğrudan
             * kökte hem de bazı ApiResponse yapılarında data altında
             * gelebilir.
             */
            val validationMessage =
                findValidationMessage(root)
                    ?: root
                        .optJSONObject("data")
                        ?.let(::findValidationMessage)
                    ?: root
                        .optJSONObject("Data")
                        ?.let(::findValidationMessage)

            if (validationMessage != null) {
                return@runCatching ApiErrorDetails(
                    code = code,
                    message = validationMessage
                )
            }

            val title =
                firstNonBlank(
                    root.optString("title"),
                    root.optString("Title")
                )

            ApiErrorDetails(
                code = code,
                message = title
            )

        }.getOrElse {
            ApiErrorDetails()
        }
    }

    fun messageOrDefault(
        errorJson: String?,
        fallback: String
    ): String {
        return parse(errorJson).message
            ?: fallback
    }

    private fun findValidationMessage(
        container: JSONObject
    ): String? {

        val errors =
            container.optJSONObject("errors")
                ?: container.optJSONObject("Errors")
                ?: return null

        /*
         * Kullanıcı formlarında sık kullanılan alanlar
         * önce kontrol edilir.
         */
        val preferredFields =
            listOf(
                "FullName",
                "fullName",
                "Email",
                "email",
                "Password",
                "password",
                "Code",
                "code",
                "NewPassword",
                "newPassword",
                "BusinessName",
                "businessName",
                "Description",
                "description",
                "DailyCapacity",
                "dailyCapacity",
                "Address",
                "address"
            )

        for (field in preferredFields) {
            validationValueToMessage(
                errors.opt(field)
            )?.let {
                return it
            }
        }

        /*
         * Beklenmeyen başka bir validation alanı varsa
         * ilk mevcut mesaj gösterilir.
         */
        val keys =
            errors.keys()

        while (keys.hasNext()) {

            val key =
                keys.next()

            validationValueToMessage(
                errors.opt(key)
            )?.let {
                return it
            }
        }

        return null
    }

    private fun validationValueToMessage(
        value: Any?
    ): String? {

        return when (value) {

            is JSONArray ->
                value
                    .optString(0)
                    .takeIf {
                        it.isNotBlank()
                    }

            is String ->
                value.takeIf {
                    it.isNotBlank()
                }

            else ->
                null
        }
    }

    private fun firstNonBlank(
        vararg values: String
    ): String? {
        return values.firstOrNull {
            it.isNotBlank()
        }
    }
}