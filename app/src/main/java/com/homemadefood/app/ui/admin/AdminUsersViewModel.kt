package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AdminRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class AdminUsersViewModel(
    private val adminRepository:
    AdminRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadUsersJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminUsersUiState()
        )

    val uiState:
            StateFlow<AdminUsersUiState> =
        _uiState.asStateFlow()

    fun loadUsers() {
        loadUsersJob?.cancel()

        loadUsersJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                val currentState =
                    _uiState.value

                try {
                    val response =
                        adminRepository.getUsers(
                            token = token,

                            role =
                                currentState
                                    .selectedRoleFilter
                                    .backendValue,

                            isActive =
                                currentState
                                    .selectedStatusFilter
                                    .backendValue,

                            search =
                                currentState
                                    .searchQuery
                                    .trim()
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,

                                users =
                                    responseBody.data
                                        .orEmpty(),

                                errorMessage = null
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Kullanıcılar alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Kullanıcılar yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun selectRoleFilter(
        filter: AdminUserRoleFilter
    ) {
        if (
            _uiState.value.selectedRoleFilter ==
            filter
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedRoleFilter = filter
            )

        loadUsers()
    }

    fun selectStatusFilter(
        filter: AdminUserStatusFilter
    ) {
        if (
            _uiState.value.selectedStatusFilter ==
            filter
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedStatusFilter = filter
            )

        loadUsers()
    }

    fun updateSearchQuery(
        value: String
    ) {
        if (value.length > 100) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                searchQuery = value
            )
    }

    fun searchUsers() {
        loadUsers()
    }

    fun clearSearch() {
        if (_uiState.value.searchQuery.isBlank()) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                searchQuery = ""
            )

        loadUsers()
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                users = emptyList(),
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (errorJson.isNullOrBlank()) {
            return null
        }

        return runCatching {
            JSONObject(errorJson)
                .optString("message")
                .takeIf { message ->
                    message.isNotBlank()
                }
        }.getOrNull()
    }
}