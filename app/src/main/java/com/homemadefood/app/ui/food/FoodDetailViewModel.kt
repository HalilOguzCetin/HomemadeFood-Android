package com.homemadefood.app.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.CartRepository
import com.homemadefood.app.data.repository.FavoriteRepository
import com.homemadefood.app.data.repository.FoodRepository
import com.homemadefood.app.data.remote.ApiErrorParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

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

        loadFoodJob =
            viewModelScope.launch {
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
                                isFavoriteChecking = true,
                                isCartChecking = true
                            )

                        /*
                         * Detay ekranı açıldığında iki state de
                         * gerçek backend verisinden okunur.
                         */
                        checkFavoriteStatus()
                        checkCartStatus()
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
                _uiState.value =
                    _uiState.value.copy(
                        isFavoriteChecking = false,
                        favoriteMessage =
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Favori durumu alınamadı.",
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

    private suspend fun checkCartStatus() {
        val isLoggedIn =
            sessionManager
                .isLoggedIn
                .first()

        if (!isLoggedIn) {
            _uiState.value =
                _uiState.value.copy(
                    isCartChecking = false,
                    cartMessage =
                        "Oturum bilgisi bulunamadı.",
                    isCartError = true
                )

            return
        }

        try {
            val response =
                cartRepository.getCart()

            val responseBody =
                response.body()

            if (
                response.isSuccessful &&
                responseBody?.success == true
            ) {
                val cart =
                    responseBody.data

                val currentItem =
                    cart?.items?.firstOrNull {
                        it.foodId == foodId
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isCartChecking = false,
                        cartItemId =
                            currentItem?.cartItemId,
                        cartQuantity =
                            currentItem?.quantity ?: 0,
                        cartMessage = null,
                        isCartError = false
                    )
            } else {
                _uiState.value =
                    _uiState.value.copy(
                        isCartChecking = false,
                        cartMessage =
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Sepet durumu alınamadı.",
                        isCartError = true
                    )
            }
        } catch (_: IOException) {
            _uiState.value =
                _uiState.value.copy(
                    isCartChecking = false,
                    cartMessage =
                        "Sepet durumu için sunucuya bağlanılamadı.",
                    isCartError = true
                )
        } catch (_: Exception) {
            _uiState.value =
                _uiState.value.copy(
                    isCartChecking = false,
                    cartMessage =
                        "Sepet durumu kontrol edilirken bir hata oluştu.",
                    isCartError = true
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

    fun refreshCartStatus() {
        if (_uiState.value.food == null) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCartChecking = true,
                    cartMessage = null,
                    isCartError = false
                )

            checkCartStatus()
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
                                responseBody.message.ifBlank {
                                    if (wasFavorite) {
                                        "Yemek favorilerden çıkarıldı."
                                    } else {
                                        "Yemek favorilere eklendi."
                                    }
                                },
                            isFavoriteError =
                                false
                        )
                } else {
                    showFavoriteError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Favori işlemi gerçekleştirilemedi."
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
            _uiState.value.isCartChecking ||
            _uiState.value.isCartActionLoading ||
            _uiState.value.food == null
        ) {
            return
        }

        if (_uiState.value.cartQuantity > 0) {
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
                    applyCartResponse(
                        cart =
                            responseBody.data,
                        message =
                            responseBody.message.ifBlank {
                                "Yemek sepete eklendi."
                            }
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

    fun increaseCartQuantity() {
        val currentQuantity =
            _uiState.value.cartQuantity

        if (currentQuantity < 1) {
            addToCart()
            return
        }

        if (currentQuantity >= 50) {
            showCartError(
                "Bir üründen en fazla 50 adet eklenebilir."
            )
            return
        }

        updateCartQuantity(
            newQuantity =
                currentQuantity + 1
        )
    }

    fun decreaseCartQuantity() {
        val currentState =
            _uiState.value

        if (
            currentState.isCartChecking ||
            currentState.isCartActionLoading
        ) {
            return
        }

        val cartItemId =
            currentState.cartItemId
                ?: return

        if (currentState.cartQuantity <= 1) {
            removeFromCart(
                cartItemId = cartItemId
            )
        } else {
            updateCartQuantity(
                newQuantity =
                    currentState.cartQuantity - 1
            )
        }
    }

    private fun updateCartQuantity(
        newQuantity: Int
    ) {
        val currentState =
            _uiState.value

        if (
            currentState.isCartChecking ||
            currentState.isCartActionLoading
        ) {
            return
        }

        val cartItemId =
            currentState.cartItemId
                ?: return

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCartActionLoading = true,
                    cartMessage = null,
                    isCartError = false
                )

            try {
                val response =
                    cartRepository.updateItem(
                        cartItemId = cartItemId,
                        quantity = newQuantity
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    applyCartResponse(
                        cart =
                            responseBody.data,
                        /*
                         * + / - işlemlerinde sürekli başarı
                         * Snackbar'ı göstermiyoruz. Miktarın
                         * ekranda değişmesi yeterli geri bildirim.
                         */
                        message = null
                    )
                } else {
                    showCartError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Ürün miktarı güncellenemedi."
                    )
                }
            } catch (_: IOException) {
                showCartError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCartError(
                    "Miktar güncellenirken bir hata oluştu."
                )
            }
        }
    }

    private fun removeFromCart(
        cartItemId: Int
    ) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCartActionLoading = true,
                    cartMessage = null,
                    isCartError = false
                )

            try {
                val response =
                    cartRepository.removeItem(
                        cartItemId = cartItemId
                    )

                val responseBody =
                    response.body()

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    responseBody.data != null
                ) {
                    applyCartResponse(
                        cart =
                            responseBody.data,
                        message =
                            "Yemek sepetten çıkarıldı."
                    )
                } else {
                    showCartError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Yemek sepetten çıkarılamadı."
                    )
                }
            } catch (_: IOException) {
                showCartError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCartError(
                    "Yemek sepetten çıkarılırken bir hata oluştu."
                )
            }
        }
    }

    private fun applyCartResponse(
        cart: com.homemadefood.app.data.model.CartResponse,
        message: String?
    ) {
        val currentItem =
            cart.items.firstOrNull {
                it.foodId == foodId
            }

        _uiState.value =
            _uiState.value.copy(
                isCartChecking = false,
                cartItemId =
                    currentItem?.cartItemId,
                cartQuantity =
                    currentItem?.quantity ?: 0,
                isCartActionLoading = false,
                cartMessage = message,
                isCartError = false
            )
    }

    fun clearFavoriteMessage() {
        _uiState.value =
            _uiState.value.copy(
                favoriteMessage = null,
                isFavoriteError = false
            )
    }

    fun clearCartMessage() {
        _uiState.value =
            _uiState.value.copy(
                cartMessage = null,
                isCartError = false
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
                isCartChecking = false,
                isCartActionLoading = false,
                cartMessage = message,
                isCartError = true
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