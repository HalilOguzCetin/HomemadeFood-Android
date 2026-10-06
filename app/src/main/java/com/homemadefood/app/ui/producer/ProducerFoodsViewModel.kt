package com.homemadefood.app.ui.producer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.data.remote.ApiErrorParser
import com.homemadefood.app.data.repository.ProducerFoodRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ProducerFoodsViewModel(
    private val producerFoodRepository:
    ProducerFoodRepository,

    private val sessionManager:
    SessionManager
) : ViewModel() {

    private var loadFoodsJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            ProducerFoodsUiState()
        )

    val uiState:
            StateFlow<ProducerFoodsUiState> =
        _uiState.asStateFlow()

    fun loadFoods() {
        loadFoodsJob?.cancel()

        loadFoodsJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        actionMessage = null,
                        actionErrorMessage = null
                    )

                val isLoggedIn =
                    sessionManager
                        .isLoggedIn
                        .first()

                if (!isLoggedIn) {
                    _uiState.value =
                        ProducerFoodsUiState(
                            isLoading = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        producerFoodRepository
                            .getMyFoods()

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        _uiState.value =
                            ProducerFoodsUiState(
                                isLoading = false,

                                foods =
                                    responseBody.data
                                        .sortedByDescending {
                                            it.id
                                        },

                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            ProducerFoodsUiState(
                                isLoading = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Yemekler alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        ProducerFoodsUiState(
                            isLoading = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        ProducerFoodsUiState(
                            isLoading = false,
                            errorMessage =
                                "Yemekler yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun toggleFoodAvailability(
        food: FoodResponse
    ) {
        if (_uiState.value.updatingFoodId != null) {
            return
        }

        val newAvailability =
            !food.isAvailable

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    updatingFoodId = food.id,
                    actionMessage = null,
                    actionErrorMessage = null
                )

            try {
                val response =
                    producerFoodRepository
                        .updateFoodAvailability(
                            foodId = food.id,
                            isAvailable = newAvailability
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    val updatedFood =
                        responseBody.data

                    _uiState.value =
                        _uiState.value.copy(
                            foods =
                                _uiState.value.foods
                                    .map { currentFood ->
                                        if (currentFood.id == updatedFood.id) {
                                            updatedFood
                                        } else {
                                            currentFood
                                        }
                                    },
                            updatingFoodId = null,
                            actionMessage =
                                responseBody.message,
                            actionErrorMessage = null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            updatingFoodId = null,
                            actionMessage = null,
                            actionErrorMessage =
                                parseErrorMessage(
                                    response.errorBody()
                                        ?.string()
                                ) ?: "Yemeğin satış durumu güncellenemedi."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        updatingFoodId = null,
                        actionMessage = null,
                        actionErrorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        updatingFoodId = null,
                        actionMessage = null,
                        actionErrorMessage =
                            "Yemeğin satış durumu güncellenirken bir hata oluştu."
                    )
            }
        }
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
