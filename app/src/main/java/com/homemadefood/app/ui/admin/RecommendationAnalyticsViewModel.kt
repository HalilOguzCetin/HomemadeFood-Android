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

class RecommendationAnalyticsViewModel(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadAnalyticsJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            RecommendationAnalyticsUiState()
        )

    val uiState:
            StateFlow<RecommendationAnalyticsUiState> =
        _uiState.asStateFlow()

    fun loadAnalytics() {
        loadAnalyticsJob?.cancel()

        loadAnalyticsJob =
            viewModelScope.launch {

                _uiState.value =
                    RecommendationAnalyticsUiState(
                        isLoading = true
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    _uiState.value =
                        RecommendationAnalyticsUiState(
                            isLoading = false,

                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        adminRepository
                            .getRecommendationAnalytics(
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
                            RecommendationAnalyticsUiState(
                                isLoading = false,

                                analytics =
                                    responseBody.data,

                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            RecommendationAnalyticsUiState(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Analiz bilgileri alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        RecommendationAnalyticsUiState(
                            isLoading = false,

                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        RecommendationAnalyticsUiState(
                            isLoading = false,

                            errorMessage =
                                "Analiz bilgileri yüklenirken bir hata oluştu."
                        )
                }
            }
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