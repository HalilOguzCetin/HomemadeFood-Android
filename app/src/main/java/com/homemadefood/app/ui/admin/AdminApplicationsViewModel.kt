package com.homemadefood.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.ProducerApplicationStatus
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
    private val adminRepository:
    AdminRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadApplicationsJob: Job? = null
    private var updateApplicationJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            AdminApplicationsUiState()
        )

    val uiState:
            StateFlow<AdminApplicationsUiState> =
        _uiState.asStateFlow()

    fun loadApplications(
        status:
        ProducerApplicationStatus =
            _uiState.value.selectedStatus
    ) {
        loadApplicationsJob?.cancel()

        loadApplicationsJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        selectedStatus = status,
                        applications = emptyList(),
                        successMessage = null,
                        errorMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showLoadError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        adminRepository
                            .getProducerApplications(
                                token = token,

                                status =
                                    status.backendValue
                            )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        val applications =
                            responseBody.data
                                .orEmpty()
                                .sortedByDescending {
                                        application ->

                                    application
                                        .producerProfileId
                                }

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                applications =
                                    applications,
                                errorMessage = null
                            )
                    } else {
                        showLoadError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Üretici başvuruları alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showLoadError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showLoadError(
                        "Başvurular yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun selectStatus(
        status: ProducerApplicationStatus
    ) {
        if (
            _uiState.value.isLoading ||
            _uiState.value
                .updatingApplicationId != null
        ) {
            return
        }

        if (
            _uiState.value.selectedStatus ==
            status
        ) {
            return
        }

        loadApplications(
            status = status
        )
    }

    fun approveApplication(
        producerProfileId: Int
    ) {
        if (!canProcessPendingApplication()) {
            return
        }

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
        if (!canProcessPendingApplication()) {
            return
        }

        val trimmedReason =
            reason.trim()

        if (
            trimmedReason.length !in 10..500
        ) {
            showActionError(
                "Red nedeni 10 ile 500 karakter arasında olmalıdır."
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

    private fun canProcessPendingApplication():
            Boolean {

        if (
            _uiState.value.selectedStatus !=
            ProducerApplicationStatus.PENDING
        ) {
            showActionError(
                "Yalnızca bekleyen başvurular işleme alınabilir."
            )

            return false
        }

        if (
            _uiState.value
                .updatingApplicationId != null
        ) {
            return false
        }

        return true
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
            producerProfileId <= 0 ||
            _uiState.value
                .updatingApplicationId != null
        ) {
            return
        }

        updateApplicationJob?.cancel()

        updateApplicationJob =
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
                    showUpdateError(
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
                        val updatedApplications =
                            _uiState.value
                                .applications
                                .filterNot {
                                        application ->

                                    application
                                        .producerProfileId ==
                                            producerProfileId
                                }

                        _uiState.value =
                            _uiState.value.copy(
                                applications =
                                    updatedApplications,

                                updatingApplicationId =
                                    null,

                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            successMessage
                                        },

                                errorMessage = null
                            )
                    } else {
                        showUpdateError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Başvuru işlemi gerçekleştirilemedi."
                        )
                    }
                } catch (_: IOException) {
                    showUpdateError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showUpdateError(
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

    private fun showLoadError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                applications = emptyList(),
                errorMessage = message,
                successMessage = null
            )
    }

    private fun showActionError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = message,
                successMessage = null
            )
    }

    private fun showUpdateError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                updatingApplicationId = null,
                errorMessage = message,
                successMessage = null
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