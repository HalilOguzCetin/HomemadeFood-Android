package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.repository.AdminRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class AdminApplicationsViewModel(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadApplicationsJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminApplicationsUiState()
        )

    val uiState:
            StateFlow<AdminApplicationsUiState> =
        _uiState.asStateFlow()

    fun loadApplications() {
        loadApplicationsJob?.cancel()

        loadApplicationsJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        successMessage = null,
                        errorMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    _uiState.value =
                        AdminApplicationsUiState(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        adminRepository
                            .getProducerApplications(
                                token = token
                            )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            AdminApplicationsUiState(
                                isLoading = false,

                                applications =
                                    responseBody.data
                                        .sortedByDescending {
                                            it.producerProfileId
                                        }
                            )
                    } else {
                        _uiState.value =
                            AdminApplicationsUiState(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Üretici başvuruları alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        AdminApplicationsUiState(
                            isLoading = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        AdminApplicationsUiState(
                            isLoading = false,
                            errorMessage =
                                "Başvurular yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun approveApplication(
        producerProfileId: Int
    ) {
        updateApplication(
            producerProfileId =
                producerProfileId,

            successMessage =
                "Üretici başvurusu onaylandı."
        ) { token ->

            adminRepository
                .approveProducerApplication(
                    token = token,

                    producerProfileId =
                        producerProfileId
                )
        }
    }

    fun rejectApplication(
        producerProfileId: Int,
        reason: String
    ) {
        val trimmedReason =
            reason.trim()

        if (
            trimmedReason.length < 10 ||
            trimmedReason.length > 500
        ) {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage =
                        "Red nedeni 10 ile 500 karakter arasında olmalıdır.",

                    successMessage = null
                )

            return
        }

        updateApplication(
            producerProfileId =
                producerProfileId,

            successMessage =
                "Üretici başvurusu reddedildi."
        ) { token ->

            adminRepository
                .rejectProducerApplication(
                    token = token,

                    producerProfileId =
                        producerProfileId,

                    reason =
                        trimmedReason
                )
        }
    }

    private fun updateApplication(
        producerProfileId: Int,
        successMessage: String,

        request:
        suspend (
            token: String
        ) -> Response<ApiResponse<Any?>>
    ) {
        if (
            _uiState.value.updatingApplicationId != null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    updatingApplicationId =
                        producerProfileId,

                    successMessage = null,
                    errorMessage = null
                )

            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                _uiState.value =
                    _uiState.value.copy(
                        updatingApplicationId = null,

                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            try {
                val response =
                    request(token)

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            applications =
                                _uiState.value
                                    .applications
                                    .filterNot {
                                        it.producerProfileId ==
                                                producerProfileId
                                    },

                            updatingApplicationId = null,

                            successMessage =
                                successMessage,

                            errorMessage = null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            updatingApplicationId = null,

                            errorMessage =
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Başvuru işlemi gerçekleştirilemedi."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        updatingApplicationId = null,

                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        updatingApplicationId = null,

                        errorMessage =
                            "Başvuru işlemi sırasında bir hata oluştu."
                    )
            }
        }
    }

    fun clearMessage() {
        _uiState.value =
            _uiState.value.copy(
                successMessage = null,
                errorMessage = null
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
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}