package com.homemadefood.app.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            FavoritesUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<FavoritesUiState> =
        _uiState.asStateFlow()



    fun loadFavorites() {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    actionMessage = null
                )

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    FavoritesUiState(
                        isLoading = false,
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            try {
                val response =
                    favoriteRepository
                        .getFavorites()

                val responseBody =
                    response.body()

                val favorites =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    favorites != null
                ) {
                    _uiState.value =
                        FavoritesUiState(
                            isLoading = false,
                            favorites = favorites,
                            errorMessage = null
                        )
                } else {
                    _uiState.value =
                        FavoritesUiState(
                            isLoading = false,

                            errorMessage =
                                if (response.code() == 401) {
                                    "Oturumunuz sona erdi. Lütfen tekrar giriş yapın."
                                } else {
                                    parseErrorMessage(
                                        response
                                            .errorBody()
                                            ?.string()
                                    )
                                        ?: "Favoriler alınamadı."
                                }
                        )
                }

            } catch (_: IOException) {
                _uiState.value =
                    FavoritesUiState(
                        isLoading = false,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )

            } catch (_: Exception) {
                _uiState.value =
                    FavoritesUiState(
                        isLoading = false,
                        errorMessage =
                            "Favoriler yüklenirken bir hata oluştu."
                    )
            }
        }
    }

    fun removeFavorite(
        foodId: Int
    ) {
        viewModelScope.launch {

            val isLoggedIn =
                sessionManager
                    .isLoggedIn
                    .first()

            if (!isLoggedIn) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage =
                            "Oturum bilgisi bulunamadı."
                    )

                return@launch
            }

            _uiState.value =
                _uiState.value.copy(
                    removingFoodId = foodId,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    favoriteRepository
                        .removeFavorite(
                            foodId = foodId
                        )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            favorites =
                                _uiState.value
                                    .favorites
                                    .filterNot {
                                        it.foodId ==
                                                foodId
                                    },

                            removingFoodId =
                                null,

                            actionMessage =
                                responseBody.message
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            removingFoodId =
                                null,

                            errorMessage =
                                if (
                                    response.code() ==
                                    401
                                ) {
                                    "Oturumunuz sona erdi. Lütfen tekrar giriş yapın."
                                } else {
                                    parseErrorMessage(
                                        response
                                            .errorBody()
                                            ?.string()
                                    )
                                        ?: "Favori silinemedi."
                                }
                        )
                }

            } catch (_: IOException) {
                _uiState.value =
                    _uiState.value.copy(
                        removingFoodId = null,
                        errorMessage =
                            "Sunucuya bağlanılamadı."
                    )

            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        removingFoodId = null,
                        errorMessage =
                            "Favori silinirken bir hata oluştu."
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