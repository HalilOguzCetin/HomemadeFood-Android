package com.homemadefood.app.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.ProducerRecommendationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import com.homemadefood.app.data.repository.CartRepository

class RecommendationViewModel(
    private val addressRepository: AddressRepository,
    private val recommendationRepository:
    ProducerRecommendationRepository,
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loadAddressesJob: Job? = null

    private var searchJob: Job? = null

    private val _uiState =
        MutableStateFlow(
            RecommendationUiState()
        )

    val uiState: StateFlow<RecommendationUiState> =
        _uiState.asStateFlow()

    fun loadAddresses() {
        loadAddressesJob?.cancel()

        loadAddressesJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoadingAddresses = true,
                        errorMessage = null,
                        actionMessage = null
                    )

                val token =
                    sessionManager.token.first()

                if (token.isNullOrBlank()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingAddresses = false,
                            errorMessage =
                                "Oturum bilgisi bulunamadı."
                        )

                    return@launch
                }

                try {
                    val response =
                        addressRepository.getAddresses(
                            token = token
                        )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        val addresses =
                            responseBody.data

                        val defaultAddress =
                            addresses.firstOrNull {
                                it.isDefault
                            }

                        val selectedAddressId =
                            defaultAddress?.id
                                ?: addresses
                                    .firstOrNull()
                                    ?.id

                        _uiState.value =
                            _uiState.value.copy(
                                isLoadingAddresses = false,
                                addresses = addresses,
                                selectedAddressId =
                                    selectedAddressId,
                                errorMessage = null
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoadingAddresses = false,

                                errorMessage =
                                    parseErrorMessage(
                                        response.errorBody()
                                            ?.string()
                                    ) ?: "Adresler alınamadı."
                            )
                    }
                } catch (_: IOException) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingAddresses = false,
                            errorMessage =
                                "Sunucuya bağlanılamadı."
                        )
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingAddresses = false,
                            errorMessage =
                                "Adresler yüklenirken bir hata oluştu."
                        )
                }
            }
    }

    fun updateSearchText(
        value: String
    ) {
        if (value.length > 100) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                searchText = value,
                errorMessage = null,
                actionMessage = null
            )
    }

    fun updateQuantityText(
        value: String
    ) {
        if (
            value.isNotEmpty() &&
            value.any {
                !it.isDigit()
            }
        ) {
            return
        }

        if (value.length > 3) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                quantityText = value,
                errorMessage = null,
                actionMessage = null
            )
    }

    fun selectAddress(
        addressId: Int
    ) {
        val addressExists =
            _uiState.value.addresses.any {
                it.id == addressId
            }

        if (!addressExists) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedAddressId = addressId,
                errorMessage = null,
                actionMessage = null
            )
    }

    fun searchRecommendations() {
        if (
            _uiState.value.isSearching ||
            _uiState.value.selectingFoodId != null
        ) {
            return
        }

        val searchText =
            _uiState.value.searchText.trim()

        val selectedAddressId =
            _uiState.value.selectedAddressId

        val quantity =
            _uiState.value.quantityText
                .toIntOrNull()

        when {
            searchText.length < 2 -> {
                showError(
                    "Arama metni en az 2 karakter olmalıdır."
                )

                return
            }

            selectedAddressId == null -> {
                showError(
                    "Lütfen teslimat adresi seçin."
                )

                return
            }

            quantity == null -> {
                showError(
                    "Lütfen geçerli bir miktar girin."
                )

                return
            }

            quantity !in 1..100 -> {
                showError(
                    "Miktar 1 ile 100 arasında olmalıdır."
                )

                return
            }
        }

        searchJob?.cancel()

        searchJob =
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
                        isSearching = true,
                        recommendationSearchId = null,
                        recommendations = emptyList(),
                        selectedRecommendation = null,
                        addedToCartFoodId = null,
                        cartMessage = null,
                        errorMessage = null,
                        actionMessage = null
                    )

                try {
                    val response =
                        recommendationRepository
                            .getRecommendations(
                                token = token,
                                searchText = searchText,
                                addressId =
                                    selectedAddressId,
                                quantity = quantity
                            )

                    val responseBody =
                        response.body()

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        responseBody.data != null
                    ) {
                        val result =
                            responseBody.data

                        _uiState.value =
                            _uiState.value.copy(
                                isSearching = false,

                                recommendationSearchId =
                                    result
                                        .recommendationSearchId,

                                recommendations =
                                    result.recommendations,

                                errorMessage = null,

                                actionMessage =
                                    if (
                                        result.recommendations
                                            .isEmpty()
                                    ) {
                                        "Arama ölçütlerine uygun üretici bulunamadı."
                                    } else {
                                        null
                                    }
                            )
                    } else {
                        showError(
                            parseErrorMessage(
                                response.errorBody()
                                    ?.string()
                            ) ?: "Üretici önerileri alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showError(
                        "Öneriler alınırken bir hata oluştu."
                    )
                }
            }
    }

    fun selectRecommendation(
        foodId: Int
    ) {
        if (
            _uiState.value.selectingFoodId != null ||
            _uiState.value.isSearching
        ) {
            return
        }

        val recommendationSearchId =
            _uiState.value.recommendationSearchId

        if (recommendationSearchId == null) {
            showError(
                "Öneri araması bulunamadı. Lütfen tekrar arama yapın."
            )

            return
        }

        val recommendationExists =
            _uiState.value.recommendations.any {
                it.foodId == foodId
            }

        if (!recommendationExists) {
            showError(
                "Seçilen öneri bulunamadı."
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
                    selectingFoodId = foodId,
                    selectedRecommendation = null,
                    addedToCartFoodId = null,
                    cartMessage = null,
                    errorMessage = null,
                    actionMessage = null
                )

            try {
                val response =
                    recommendationRepository
                        .selectRecommendation(
                            token = token,

                            recommendationSearchId =
                                recommendationSearchId,

                            foodId = foodId
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
                            selectingFoodId = null,

                            selectedRecommendation =
                                responseBody.data,

                            actionMessage =
                                responseBody.message
                                    .ifBlank {
                                        "Öneri seçildi."
                                    },

                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Öneri seçilemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Öneri seçilirken bir hata oluştu."
                )
            }
        }
    }
    fun addSelectedRecommendationToCart() {
        if (
            _uiState.value.isAddingToCart ||
            _uiState.value.isSearching ||
            _uiState.value.selectingFoodId != null
        ) {
            return
        }

        val selectedRecommendation =
            _uiState.value.selectedRecommendation
        if (
            selectedRecommendation != null &&
            _uiState.value.addedToCartFoodId ==
            selectedRecommendation.foodId
        ) {
            showError(
                "Bu öneri zaten sepete eklendi."
            )

            return
        }

        val recommendationSearchId =
            _uiState.value.recommendationSearchId

        val quantity =
            _uiState.value.quantityText
                .toIntOrNull()

        when {
            selectedRecommendation == null -> {
                showError(
                    "Önce önerilerden birini seçin."
                )

                return
            }

            recommendationSearchId == null -> {
                showError(
                    "Öneri araması bulunamadı. Tekrar arama yapın."
                )

                return
            }

            quantity == null ||
                    quantity !in 1..100 -> {

                showError(
                    "Geçerli bir miktar girin."
                )

                return
            }
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
                    isAddingToCart = true,
                    cartMessage = null,
                    errorMessage = null
                )

            try {
                val response =
                    cartRepository.addItem(
                        token = token,

                        foodId =
                            selectedRecommendation.foodId,

                        quantity = quantity,

                        recommendationSearchId =
                            recommendationSearchId
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
                            isAddingToCart = false,

                            addedToCartFoodId =
                                selectedRecommendation.foodId,

                            cartMessage =
                                responseBody.message
                                    .ifBlank {
                                        "Seçilen öneri sepete eklendi."
                                    },

                            errorMessage = null
                        )
                } else {
                    showError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Öneri sepete eklenemedi."
                    )
                }
            } catch (_: IOException) {
                showError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showError(
                    "Öneri sepete eklenirken bir hata oluştu."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null,
                actionMessage = null,
                cartMessage = null
            )
    }

    private fun showError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isLoadingAddresses = false,
                isSearching = false,
                isAddingToCart = false,
                selectingFoodId = null,
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
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }
}