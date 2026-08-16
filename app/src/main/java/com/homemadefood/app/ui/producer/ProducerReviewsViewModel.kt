package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.ProducerRepository
import com.homemadefood.app.data.repository.ReviewRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ProducerReviewsViewModel(
    private val producerRepository:
    ProducerRepository,

    private val reviewRepository:
    ReviewRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadReviewsJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerReviewsUiState()
        )

    val uiState:
            StateFlow<ProducerReviewsUiState> =
        _uiState.asStateFlow()

    fun loadReviews() {
        loadReviewsJob?.cancel()

        loadReviewsJob =
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
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val applicationResponse =
                        producerRepository
                            .getMyApplication()

                    val applicationBody =
                        applicationResponse.body()

                    val application =
                        applicationBody?.data

                    if (
                        !applicationResponse.isSuccessful ||
                        applicationBody?.success != true ||
                        application == null
                    ) {
                        showError(
                            parseErrorMessage(
                                applicationResponse
                                    .errorBody()
                                    ?.string()
                            ) ?: "Üretici profil bilgisi alınamadı."
                        )

                        return@launch
                    }

                    val producerProfileId =
                        application.producerProfileId

                    if (producerProfileId <= 0) {
                        showError(
                            "Geçerli üretici profil bilgisi bulunamadı."
                        )

                        return@launch
                    }

                    val reviewsResponse =
                        reviewRepository
                            .getProducerReviews(
                                producerProfileId =
                                    producerProfileId
                            )

                    val reviewsBody =
                        reviewsResponse.body()

                    if (
                        reviewsResponse.isSuccessful &&
                        reviewsBody?.success == true
                    ) {
                        val reviews =
                            reviewsBody.data
                                .orEmpty()
                                .sortedByDescending {
                                        review ->
                                    review.reviewId
                                }

                        _uiState.value =
                            ProducerReviewsUiState(
                                isLoading = false,

                                producerProfileId =
                                    producerProfileId,

                                reviews = reviews,

                                errorMessage = null
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                reviewsResponse
                                    .errorBody()
                                    ?.string()
                            ) ?: "Değerlendirmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Değerlendirmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        return ApiErrorParser
            .parse(
                errorJson
            )
            .message
    }
}