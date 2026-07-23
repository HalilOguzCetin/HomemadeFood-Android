package com.homemadefood.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.homemadefood.app.data.model.LoginResponse
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
    }

    val token: Flow<String?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }

    val userId: Flow<Int?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }

    val fullName: Flow<String?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[FULL_NAME_KEY]
        }

    val email: Flow<String?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[EMAIL_KEY]
        }

    val role: Flow<String?> =
        context.sessionDataStore.data.map { preferences ->
            preferences[ROLE_KEY]
        }

    val isLoggedIn: Flow<Boolean> =
        context.sessionDataStore.data.map { preferences ->
            !preferences[TOKEN_KEY].isNullOrBlank()
        }

    suspend fun saveSession(
        loginResponse: LoginResponse
    ) {
        context.sessionDataStore.edit { preferences ->
            preferences[TOKEN_KEY] =
                loginResponse.token

            preferences[USER_ID_KEY] =
                loginResponse.userId

            preferences[FULL_NAME_KEY] =
                loginResponse.fullName

            preferences[EMAIL_KEY] =
                loginResponse.email

            preferences[ROLE_KEY] =
                loginResponse.role
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}