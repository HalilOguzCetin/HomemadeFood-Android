package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.ProducerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ProducerApplicationViewModel(
    private val producerRepository: ProducerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadApplicationJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerApplicationUiState()
        )

    val uiState:
            StateFlow<ProducerApplicationUiState> =
        _uiState.asStateFlow()

    fun loadApplication() {
        loadApplicationJob?.cancel()

        loadApplicationJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    _uiState.value =
                        ProducerApplicationUiState(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        producerRepository
                            .getMyApplication()

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            ProducerApplicationUiState(
                                isLoading = false,
                                application =
                                    responseBody.data,
                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            ProducerApplicationUiState(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Üretici başvuru bilgisi alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        ProducerApplicationUiState(
                            isLoading = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        ProducerApplicationUiState(
                            isLoading = false,
                            errorMessage =
                                "Başvuru bilgisi yüklenirken bir hata oluştu."
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