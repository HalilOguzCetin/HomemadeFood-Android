package com.homemadefood.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.homemadefood.app.data.model.AppMode
import com.homemadefood.app.data.model.LoginResponse
import com.homemadefood.app.data.model.UserProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(
    name = "user_session"
)

class SessionManager(
    private val context: Context
) {

    private companion object {

        val TOKEN_KEY =
            stringPreferencesKey("jwt_token")

        val USER_ID_KEY =
            intPreferencesKey("user_id")

        val FULL_NAME_KEY =
            stringPreferencesKey("full_name")

        val EMAIL_KEY =
            stringPreferencesKey("email")

        val ROLE_KEY =
            stringPreferencesKey("role")

        val CAN_USE_PRODUCER_MODE_KEY =
            booleanPreferencesKey(
                "can_use_producer_mode"
            )

        val PRODUCER_PROFILE_ID_KEY =
            intPreferencesKey(
                "producer_profile_id"
            )

        val PRODUCER_VERIFICATION_STATUS_KEY =
            stringPreferencesKey(
                "producer_verification_status"
            )

        val ACTIVE_MODE_KEY =
            stringPreferencesKey(
                "active_app_mode"
            )
    }

    val token: Flow<String?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[TOKEN_KEY]
        }

    val userId: Flow<Int?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[USER_ID_KEY]
        }

    val fullName: Flow<String?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[FULL_NAME_KEY]
        }

    val email: Flow<String?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[EMAIL_KEY]
        }

    val role: Flow<String?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[ROLE_KEY]
        }

    val canUseProducerMode: Flow<Boolean> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[
                CAN_USE_PRODUCER_MODE_KEY
            ] ?: false
        }

    val producerProfileId: Flow<Int?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[
                PRODUCER_PROFILE_ID_KEY
            ]
        }

    val producerVerificationStatus:
            Flow<String?> =
        context.sessionDataStore.data.map {
                preferences ->
            preferences[
                PRODUCER_VERIFICATION_STATUS_KEY
            ]
        }

    val activeMode: Flow<AppMode?> =
        context.sessionDataStore.data.map {
                preferences ->

            AppMode.fromStoredValue(
                preferences[ACTIVE_MODE_KEY]
            )
        }

    val isLoggedIn: Flow<Boolean> =
        context.sessionDataStore.data.map {
                preferences ->

            !preferences[TOKEN_KEY]
                .isNullOrBlank()
        }

    suspend fun saveSession(
        loginResponse: LoginResponse
    ) {
        context.sessionDataStore.edit {
                preferences ->

            preferences[TOKEN_KEY] =
                loginResponse.token

            preferences[USER_ID_KEY] =
                loginResponse.userId

            preferences[FULL_NAME_KEY] =
                loginResponse.fullName

            preferences[EMAIL_KEY] =
                loginResponse.email

            preferences[ROLE_KEY] =
                loginResponse.role.trim()

            preferences[
                CAN_USE_PRODUCER_MODE_KEY
            ] =
                loginResponse
                    .canUseProducerMode

            saveProducerInformation(
                preferences =
                    preferences,

                producerProfileId =
                    loginResponse
                        .producerProfileId,

                verificationStatus =
                    loginResponse
                        .producerVerificationStatus
            )

            /*
             * Normal girişte güvenli varsayılan
             * olarak müşteri modu açılır.
             *
             * Admin hesabı için aktif müşteri veya
             * üretici modu saklanmaz.
             */
            if (
                loginResponse.role.equals(
                    "Customer",
                    ignoreCase = true
                )
            ) {
                preferences[ACTIVE_MODE_KEY] =
                    AppMode.CUSTOMER.name
            } else {
                preferences.remove(
                    ACTIVE_MODE_KEY
                )
            }
        }
    }

    suspend fun updateProfile(
        profile: UserProfileResponse
    ) {
        context.sessionDataStore.edit {
                preferences ->

            preferences[USER_ID_KEY] =
                profile.userId

            preferences[FULL_NAME_KEY] =
                profile.fullName

            preferences[EMAIL_KEY] =
                profile.email

            preferences[ROLE_KEY] =
                profile.role.trim()

            preferences[
                CAN_USE_PRODUCER_MODE_KEY
            ] =
                profile.canUseProducerMode

            saveProducerInformation(
                preferences =
                    preferences,

                producerProfileId =
                    profile.producerProfileId,

                verificationStatus =
                    profile
                        .producerVerificationStatus
            )

            val currentMode =
                AppMode.fromStoredValue(
                    preferences[
                        ACTIVE_MODE_KEY
                    ]
                )

            if (
                profile.role.equals(
                    "Admin",
                    ignoreCase = true
                )
            ) {
                preferences.remove(
                    ACTIVE_MODE_KEY
                )
            } else if (
                currentMode ==
                AppMode.PRODUCER &&
                !profile.canUseProducerMode
            ) {
                /*
                 * Üretici yetkisi kaldırılmışsa
                 * eski Producer modunda kalınamaz.
                 */
                preferences[ACTIVE_MODE_KEY] =
                    AppMode.CUSTOMER.name
            } else if (currentMode == null) {
                preferences[ACTIVE_MODE_KEY] =
                    AppMode.CUSTOMER.name
            }
        }
    }

    suspend fun setActiveMode(
        requestedMode: AppMode
    ) {
        context.sessionDataStore.edit {
                preferences ->

            val storedRole =
                preferences[ROLE_KEY]

            if (
                storedRole.equals(
                    "Admin",
                    ignoreCase = true
                )
            ) {
                /*
                 * Admin hesabında müşteri veya
                 * üretici modu kullanılamaz.
                 */
                preferences.remove(
                    ACTIVE_MODE_KEY
                )

                return@edit
            }

            val canUseProducerMode =
                preferences[
                    CAN_USE_PRODUCER_MODE_KEY
                ] ?: false

            val safeMode =
                if (
                    requestedMode ==
                    AppMode.PRODUCER &&
                    !canUseProducerMode
                ) {
                    AppMode.CUSTOMER
                } else {
                    requestedMode
                }

            preferences[ACTIVE_MODE_KEY] =
                safeMode.name
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit {
                preferences ->
            preferences.clear()
        }
    }

    private fun saveProducerInformation(
        preferences:
        androidx.datastore.preferences.core
        .MutablePreferences,

        producerProfileId: Int?,
        verificationStatus: String?
    ) {
        if (producerProfileId != null) {
            preferences[
                PRODUCER_PROFILE_ID_KEY
            ] =
                producerProfileId
        } else {
            preferences.remove(
                PRODUCER_PROFILE_ID_KEY
            )
        }

        if (
            !verificationStatus
                .isNullOrBlank()
        ) {
            preferences[
                PRODUCER_VERIFICATION_STATUS_KEY
            ] =
                verificationStatus
                    .trim()
        } else {
            preferences.remove(
                PRODUCER_VERIFICATION_STATUS_KEY
            )
        }
    }
}