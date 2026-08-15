package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.model.ProducerStorefrontSummaryResponse
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.StorefrontRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerHomeViewModel(
    private val categoryRepository:
    CategoryRepository,

    private val storefrontRepository:
    StorefrontRepository
) : ViewModel() {

    private var storefrontLoadJob: Job? =
        null

    /*
     * Backend kategori filtresini uygular.
     * Arama metni ise o anda yüklenmiş işletmeler üzerinde
     * yalnızca vitrin adı/açıklama/konum alanlarında uygulanır.
     *
     * AŞAMA 4'ün amacı yemek araması değil,
     * işletme keşif ekranıdır.
     */
    private var loadedStorefronts:
            List<ProducerStorefrontSummaryResponse> =
        emptyList()

    private val _uiState =
        MutableStateFlow(
            CustomerHomeUiState(
                isCategoriesLoading = true,
                isStorefrontsLoading = true
            )
        )

    val uiState:
            StateFlow<CustomerHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadCategories()
        loadStorefronts()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCategoriesLoading = true,
                    categoryErrorMessage = null
                )

            try {
                val response =
                    categoryRepository
                        .getCategories()

                val responseBody =
                    response.body()

                val categories =
                    responseBody?.data

                if (
                    response.isSuccessful &&
                    responseBody?.success == true &&
                    categories != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isCategoriesLoading = false,

                            categories =
                                categories.filter {
                                    it.isActive != false
                                },

                            categoryErrorMessage = null
                        )
                } else {
                    showCategoryError(
                        parseErrorMessage(
                            response.errorBody()
                                ?.string()
                        ) ?: "Kategoriler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showCategoryError(
                    "Sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCategoryError(
                    "Kategoriler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    fun loadStorefronts(
        categoryId: Int? =
            _uiState.value.selectedCategoryId
    ) {
        storefrontLoadJob?.cancel()

        storefrontLoadJob =
            viewModelScope.launch {
                loadedStorefronts =
                    emptyList()

                _uiState.value =
                    _uiState.value.copy(
                        isStorefrontsLoading = true,
                        storefronts = emptyList(),
                        storefrontErrorMessage = null
                    )

                try {
                    val response =
                        storefrontRepository
                            .getStorefronts(
                                categoryId =
                                    categoryId
                            )

                    val responseBody =
                        response.body()

                    val storefronts =
                        responseBody?.data

                    if (
                        response.isSuccessful &&
                        responseBody?.success == true &&
                        storefronts != null
                    ) {
                        loadedStorefronts =
                            storefronts

                        _uiState.value =
                            _uiState.value.copy(
                                isStorefrontsLoading = false,
                                storefrontErrorMessage = null
                            )

                        applySearchFilter()
                    } else {
                        showStorefrontError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "İşletmeler alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showStorefrontError(
                        "Sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showStorefrontError(
                        "İşletmeler yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    fun updateSearchQuery(
        query: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                searchQuery = query
            )
    }

    fun searchStorefronts() {
        applySearchFilter()
    }

    fun selectCategory(
        categoryId: Int?
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId =
                    categoryId
            )

        loadStorefronts(
            categoryId = categoryId
        )
    }

    fun clearFilters() {
        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId = null,
                searchQuery = ""
            )

        loadStorefronts(
            categoryId = null
        )
    }

    private fun applySearchFilter() {
        val query =
            _uiState.value
                .searchQuery
                .trim()

        val filtered =
            if (query.isBlank()) {
                loadedStorefronts
            } else {
                loadedStorefronts.filter {
                        storefront ->

                    storefront.businessName
                        .contains(
                            query,
                            ignoreCase = true
                        ) ||
                            storefront.description
                                .contains(
                                    query,
                                    ignoreCase = true
                                ) ||
                            storefront.city
                                .contains(
                                    query,
                                    ignoreCase = true
                                ) ||
                            storefront.district
                                .contains(
                                    query,
                                    ignoreCase = true
                                )
                }
            }

        _uiState.value =
            _uiState.value.copy(
                storefronts = filtered
            )
    }

    private fun showCategoryError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading = false,
                categoryErrorMessage = message
            )
    }

    private fun showStorefrontError(
        message: String
    ) {
        loadedStorefronts =
            emptyList()

        _uiState.value =
            _uiState.value.copy(
                isStorefrontsLoading = false,
                storefronts = emptyList(),
                storefrontErrorMessage = message
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