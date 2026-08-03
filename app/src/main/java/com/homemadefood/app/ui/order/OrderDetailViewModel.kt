package com.homemadefood.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.OrderStatus
import com.homemadefood.app.data.repository.OrderRepository
import com.homemadefood.app.data.repository.ReviewRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class OrderDetailViewModel(
    private val orderId: Int,
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadOrderJob: Job? = null
    private var submitReviewJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            OrderDetailUiState()
        )

    val uiState: StateFlow<OrderDetailUiState> =
        _uiState.asStateFlow()

    fun loadOrder() {
        loadOrderJob?.cancel()

        loadOrderJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        isReviewStatusLoading = false,
                        hasCheckedReview = false,
                        existingReview = null,
                        isReviewFormVisible = false,
                        selectedRating = 0,
                        reviewComment = "",
                        errorMessage = null,
                        actionMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                try {
                    val response =
                        orderRepository.getOrderById(
                            token = token,
                            orderId = orderId
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        val loadedOrder =
                            responseBody.data

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                order = loadedOrder,
                                errorMessage = null
                            )

                        if (
                            loadedOrder.orderStatus ==
                            OrderStatus.DELIVERED
                        ) {
                            loadExistingReview(
                                token = token,
                                targetOrderId =
                                    loadedOrder.orderId
                            )
                        }
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Sipariş detayı alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Sipariş detayı yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    private suspend fun loadExistingReview(
        token: String,
        targetOrderId: Int
    ) {
        _uiState.value =
            _uiState.value.copy(
                isReviewStatusLoading = true,
                hasCheckedReview = false
            )

        try {
            val response =
                reviewRepository.getMyReviews(
                    token = token
                )

            val responseBody =
                response.body()

            if (
                response.isSuccessful &&
                responseBody?.success == true &&
                responseBody.data != null
            ) {
                val existingReview =
                    responseBody.data
                        .firstOrNull { review ->
                            review.orderId ==
                                    targetOrderId
                        }

                _uiState.value =
                    _uiState.value.copy(
                        isReviewStatusLoading = false,
                        hasCheckedReview = true,
                        existingReview =
                            existingReview,
                        errorMessage = null
                    )
            } else {
                _uiState.value =
                    _uiState.value.copy(
                        isReviewStatusLoading = false,
                        hasCheckedReview = false,

                        errorMessage =
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Değerlendirme durumu alınamadı."
                    )
            }
        } catch (_: IOException) {
            _uiState.value =
                _uiState.value.copy(
                    isReviewStatusLoading = false,
                    hasCheckedReview = false,
                    errorMessage =
                        "Değerlendirme bilgisi için sunucuya bağlanılamadı."
                )
        } catch (_: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isReviewStatusLoading = false,
                    hasCheckedReview = false,
                    errorMessage =
                        "Değerlendirme bilgisi yüklenirken bir hata oluştu."
                )
        }
    }

    fun cancelOrder() {
        if (_uiState.value.isCancelling) {
            return
        }

        val currentOrder =
            _uiState.value.order

        if (currentOrder == null) {
            showError(
                "Sipariş bilgisi bulunamadı."
            )

            return
        }

        if (
            currentOrder.orderStatus !=
            OrderStatus.PENDING
        ) {
            showError(
                "Yalnızca onay bekleyen siparişler iptal edilebilir."
            )

            return
        }

        viewModelScope.launch {
            val token =
                sessionManager.token.first()

            if (token.isNullOrBlank()) {
                showError(
                    "Oturum bilgisi bulunamadı."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    isCancelling = true,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    orderRepository.cancelOrder(
                        token = token,
                        orderId = orderId
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            order = responseBody.data,
                            isCancelling = false,

                            actionMessage =
                                responseBody.message
                                    .ifBlank {
                                        "Sipariş iptal edildi."
                                    },

                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Sipariş iptal edilemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Sipariş iptal edilirken bir hata oluştu."
                )
            }
        }
    }

    fun showReviewForm() {
        val currentOrder =
            _uiState.value.order

        when {
            currentOrder == null -> {
                showError(
                    "Sipariş bilgisi bulunamadı."
                )
            }

            currentOrder.orderStatus !=
                    OrderStatus.DELIVERED -> {

                showError(
                    "Yalnızca teslim edilmiş siparişler değerlendirilebilir."
                )
            }

            !_uiState.value.hasCheckedReview -> {
                showError(
                    "Değerlendirme durumu henüz doğrulanmadı."
                )
            }

            _uiState.value.existingReview != null -> {
                showError(
                    "Bu sipariş daha önce değerlendirilmiş."
                )
            }

            else -> {
                _uiState.value =
                    _uiState.value.copy(
                        isReviewFormVisible = true,
                        errorMessage = null,
                        actionMessage = null
                    )
            }
        }
    }

    fun hideReviewForm() {
        if (_uiState.value.isSubmittingReview) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isReviewFormVisible = false,
                errorMessage = null
            )
    }

    fun selectRating(
        rating: Int
    ) {
        if (
            rating !in 1..5 ||
            _uiState.value.isSubmittingReview
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedRating = rating,
                errorMessage = null
            )
    }

    fun updateReviewComment(
        value: String
    ) {
        if (
            value.length > 1000 ||
            _uiState.value.isSubmittingReview
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                reviewComment = value,
                errorMessage = null
            )
    }

    fun submitReview() {
        if (
            _uiState.value.isSubmittingReview ||
            _uiState.value.isLoading
        ) {
            return
        }

        val currentOrder =
            _uiState.value.order

        val rating =
            _uiState.value.selectedRating

        val comment =
            _uiState.value.reviewComment.trim()

        when {
            currentOrder == null -> {
                showError(
                    "Sipariş bilgisi bulunamadı."
                )

                return
            }

            currentOrder.orderStatus !=
                    OrderStatus.DELIVERED -> {

                showError(
                    "Yalnızca teslim edilmiş siparişler değerlendirilebilir."
                )

                return
            }

            _uiState.value.existingReview != null -> {
                showError(
                    "Bu sipariş daha önce değerlendirilmiş."
                )

                return
            }

            rating !in 1..5 -> {
                showError(
                    "Lütfen 1 ile 5 arasında bir puan seçin."
                )

                return
            }

            comment.length > 1000 -> {
                showError(
                    "Yorum en fazla 1000 karakter olabilir."
                )

                return
            }
        }

        submitReviewJob?.cancel()

        submitReviewJob =
            viewModelScope.launch {
                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    showError(
                        "Oturum bilgisi bulunamadı."
                    )

                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingReview = true,
                        errorMessage = null,
                        actionMessage = null
                    )

                try {
                    val response =
                        reviewRepository.createReview(
                            token = token,
                            orderId = currentOrder.orderId,
                            rating = rating,
                            comment = comment
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            _uiState.value.copy(
                                isSubmittingReview = false,
                                isReviewFormVisible = false,
                                hasCheckedReview = true,

                                existingReview =
                                    responseBody.data,

                                actionMessage =
                                    responseBody.message
                                        .ifBlank {
                                            "Değerlendirmeniz kaydedildi."
                                        },

                                errorMessage = null
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Değerlendirme gönderilemedi."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Değerlendirme gönderilirken bir hata oluştu."
                    )
                }
            }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                actionMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isCancelling = false,
                isSubmittingReview = false,
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