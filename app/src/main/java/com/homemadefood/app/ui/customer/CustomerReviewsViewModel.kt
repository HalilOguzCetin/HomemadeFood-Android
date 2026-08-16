package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.ReviewResponse
import com.homemadefood.app.data.repository.ReviewRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class CustomerReviewsViewModel(
    private val reviewRepository:
    ReviewRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadReviewsJob: Job? = null
    private var deleteReviewJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            CustomerReviewsUiState()
        )

    val uiState:
            StateFlow<CustomerReviewsUiState> =
        _uiState.asStateFlow()

    fun loadReviews() {
        loadReviewsJob?.cancel()

        loadReviewsJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
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
                    val response =
                        reviewRepository
                            .getMyReviews()

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        val reviews =
                            responseBody.data
                                .orEmpty()
                                .sortedByDescending {
                                        review ->
                                    review.reviewId
                                }

                        _uiState.value =
                            CustomerReviewsUiState(
                                isLoading = false,
                                reviews = reviews
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
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

    fun requestDeleteReview(
        review: ReviewResponse
    ) {
        if (
            _uiState.value.deletingReviewId != null
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                reviewPendingDeletion = review,
                errorMessage = null,
                successMessage = null
            )
    }

    fun dismissDeleteDialog() {
        if (
            _uiState.value.deletingReviewId != null
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                reviewPendingDeletion = null
            )
    }

    fun confirmDeleteReview() {
        if (
            _uiState.value.deletingReviewId != null
        ) {
            return
        }

        val review =
            _uiState.value
                .reviewPendingDeletion
                ?: return

        if (review.reviewId <= 0) {
            showError(
                "Geçerli değerlendirme bilgisi bulunamadı."
            )

            return
        }

        deleteReviewJob?.cancel()

        deleteReviewJob =
            viewModelScope.launch {
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

                _uiState.value =
                    _uiState.value.copy(
                        deletingReviewId =
                            review.reviewId,

                        errorMessage = null,
                        successMessage = null
                    )

                try {
                    val response =
                        reviewRepository.deleteReview(
                            reviewId = review.reviewId
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true
                    ) {
                        val updatedReviews =
                            _uiState.value.reviews
                                .filterNot {
                                        currentReview ->

                                    currentReview.reviewId ==
                                            review.reviewId
                                }

                        _uiState.value =
                            _uiState.value.copy(
                                reviews = updatedReviews,

                                reviewPendingDeletion =
                                    null,

                                deletingReviewId =
                                    null,

                                successMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "Değerlendirme başarıyla silindi."
                                        },

                                errorMessage = null
                            )
                    } else {
                        showDeleteError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Değerlendirme silinemedi."
                        )
                    }
                } catch (_: IOException) {
                    showDeleteError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showDeleteError(
                        "Değerlendirme silinirken bir hata oluştu."
                    )
                }
            }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                successMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                deletingReviewId = null,
                errorMessage = message
            )
    }

    private fun showDeleteError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                deletingReviewId = null,
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