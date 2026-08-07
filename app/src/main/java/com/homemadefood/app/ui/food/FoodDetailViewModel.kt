package com.homemadefood.app.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.FavoriteRepository
import com.homemadefood.app.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.Job
import com.homemadefood.app.data.repository.CartRepository

class FoodDetailViewModel(
    private val foodId: Int,
    private val foodRepository: FoodRepository,
    private val favoriteRepository: FavoriteRepository,
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadFoodJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            FoodDetailUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<FoodDetailUiState> =
        _uiState.asStateFlow()



    fun loadFood() {
        loadFoodJob?.cancel()

        loadFoodJob = viewModelScope.launch {
            _uiState.value =
                FoodDetailUiState(
                    isLoading = true
                )

            try {
                val response =
                    foodRepository.getFoodById(
                        foodId = foodId
                    )

                val responseBody =
                    response.body()

                val food =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    food != null
                ) {
                    _uiState.value =
                        FoodDetailUiState(
                            isLoading = false,
                            food = food,
                            errorMessage = null,
                            isFavoriteChecking = true
                        )

                    checkFavoriteStatus()
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        )

                    _uiState.value =
                        FoodDetailUiState(
                            isLoading = false,
                            food = null,
                            errorMessage =
                                errorMessage
                                    ?: "Yemek bilgisi alınamadı."
                        )
                }
            } catch (_: IOException) {
                _uiState.value =
                    FoodDetailUiState(
                        isLoading = false,
                        food = null,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )
            } catch (_: Exception) {
                _uiState.value =
                    FoodDetailUiState(
                        isLoading = false,
                        food = null,
                        errorMessage =
                            "Yemek bilgisi yüklenirken bir hata oluştu."
                    )
            }
        }
    }

    private suspend fun checkFavoriteStatus() {
        val isLoggedIn =
            sessionManager
                .isLoggedIn
                .first()

        if (!isLoggedIn) {
            _uiState.value =
                _uiState.value.copy(
                    isFavoriteChecking = false,
                    favoriteMessage =
                        "Oturum bilgisi bulunamadı.",
                    isFavoriteError = true
                )

            return
        }

        try {
            val response =
                favoriteRepository.getFavorites()

            val responseBody =
                response.body()

            val favorites =
                responseBody?.data

            if (
                response.isSuccessful &&
                responseBody?.success == true &&
                favorites != null
            ) {
                val isFavorite =
                    favorites.any {
                        it.foodId == foodId
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isFavorite = isFavorite,
                        isFavoriteChecking = false,
                        favoriteMessage = null,
                        isFavoriteError = false
                    )
            } else {
                val errorMessage =
                    parseErrorMessage(
                        response.errorBody()
                            ?.string()
                    )

                _uiState.value =
                    _uiState.value.copy(
                        isFavoriteChecking = false,
                        favoriteMessage =
                            errorMessage
                                ?: "Favori durumu alınamadı.",
                        isFavoriteError = true
                    )
            }
        } catch (_: IOException) {
            _uiState.value =
                _uiState.value.copy(
                    isFavoriteChecking = false,
                    favoriteMessage =
                        "Favori durumu için sunucuya bağlanılamadı.",
                    isFavoriteError = true
                )
        } catch (_: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isFavoriteChecking = false,
                    favoriteMessage =
                        "Favori durumu kontrol edilirken bir hata oluştu.",
                    isFavoriteError = true
                )
        }
    }
    fun refreshFavoriteStatus() {
        if (_uiState.value.food == null) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isFavoriteChecking = true,
                    favoriteMessage = null,
                    isFavoriteError = false
                )

            checkFavoriteStatus()
        }
    }

    fun toggleFavorite() {
        if (
            _uiState.value.isFavoriteChecking ||
            _uiState.value.isFavoriteActionLoading
        ) {
            return
        }

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                showFavoriteError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )

                return@launch
            }

            val wasFavorite =
                _uiState.value.isFavorite

            _uiState.value =
                _uiState.value.copy(
                    isFavoriteActionLoading = true,
                    favoriteMessage = null,
                    isFavoriteError = false
                )

            try {
                val response =
                    if (wasFavorite) {
                        favoriteRepository.removeFavorite(
                            foodId = foodId
                        )
                    } else {
                        favoriteRepository.addFavorite(
                            foodId = foodId
                        )
                    }

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isFavorite =
                                !wasFavorite,

                            isFavoriteActionLoading =
                                false,

                            favoriteMessage =
                                responseBody.message,

                            isFavoriteError =
                                false
                        )
                } else {
                    val errorMessage =
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        )

                    showFavoriteError(
                        errorMessage
                            ?: "Favori işlemi gerçekleştirilemedi."
                    )
                }
            } catch (_: IOException) {
                showFavoriteError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showFavoriteError(
                    "Favori işlemi sırasında bir hata oluştu."
                )
            }
        }
    }
    fun addToCart() {
        if (
            _uiState.value.isCartActionLoading ||
            _uiState.value.food == null
        ) {
            return
        }

        viewModelScope.launch {
            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                showCartError(
                    "Oturum bilgisi bulunamadı. Yeniden giriş yapın."
                )

                return@launch
            }

            val food =
                _uiState.value.food

            if (food?.isAvailable != true) {
                showCartError(
                    "Bu yemek şu anda satışta değil."
                )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    isCartActionLoading = true,
                    cartMessage = null,
                    isCartError = false
                )

            try {
                val response =
                    cartRepository.addItem(
                        foodId = foodId,
                        quantity = 1,
                        recommendationSearchId = null
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
                            isCartActionLoading = false,
                            cartMessage =
                                responseBody.message.ifBlank {
                                    "Yemek sepete eklendi."
                                },
                            isCartError = false
                        )
                } else {
                    showCartError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Yemek sepete eklenemedi."
                    )
                }
            } catch (_: IOException) {
                showCartError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCartError(
                    "Sepete ekleme sırasında bir hata oluştu."
                )
            }
        }
    }

    fun clearFavoriteMessage() {
        _uiState.value =
            _uiState.value.copy(
                favoriteMessage = null,
                isFavoriteError = false
            )
    }

    private fun showFavoriteError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isFavoriteActionLoading = false,
                favoriteMessage = message,
                isFavoriteError = true
            )
    }
    private fun showCartError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCartActionLoading = false,
                cartMessage = message,
                isCartError = true
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