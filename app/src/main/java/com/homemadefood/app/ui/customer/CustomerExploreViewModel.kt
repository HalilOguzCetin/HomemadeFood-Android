package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.FoodRepository
import com.homemadefood.app.data.repository.StorefrontRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class CustomerExploreViewModel(
    private val categoryRepository:
    CategoryRepository,

    private val foodRepository:
    FoodRepository,

    private val storefrontRepository:
    StorefrontRepository,

    private val addressRepository:
    AddressRepository,

    private val deliveryAddressSelectionManager:
    DeliveryAddressSelectionManager
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
        const val DISCOVER_RADIUS_KM =
            30.0
    }

    private val _uiState =
        MutableStateFlow(
            CustomerExploreUiState()
        )

    val uiState:
            StateFlow<CustomerExploreUiState> =
        _uiState.asStateFlow()

    private var addressLoadJob: Job? =
        null

    private var initialResultLoadJob:
            Job? = null

    private var foodLoadMoreJob:
            Job? = null

    private var storefrontLoadMoreJob:
            Job? = null

    init {
        loadCategories()
    }

    /*
     * Keşfet destination her yeniden görünür olduğunda çağrılır.
     *
     * Böylece kullanıcı Adreslerim ekranına gidip aktif adresini
     * değiştirdikten sonra Keşfet eski koordinatla devam etmez.
     */
    fun refreshDeliveryContext() {
        addressLoadJob
            ?.cancel()

        addressLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isDeliveryAddressLoading =
                            true,

                        deliveryAddressErrorMessage =
                            null
                    )

                try {
                    val response =
                        addressRepository
                            .getAddresses()

                    val body =
                        response.body()

                    val addresses =
                        body?.data

                    if (
                        response.isSuccessful &&
                        body?.success == true &&
                        addresses != null
                    ) {
                        val selectedAddress =
                            deliveryAddressSelectionManager
                                .resolve(
                                    addresses
                                )

                        _uiState.value =
                            _uiState.value.copy(
                                isDeliveryAddressLoading =
                                    false,

                                deliveryAddresses =
                                    addresses,

                                selectedDeliveryAddress =
                                    selectedAddress,

                                deliveryAddressErrorMessage =
                                    null
                            )

                        if (
                            selectedAddress == null
                        ) {
                            clearDiscoverResults()
                        } else {
                            loadInitialResults(
                                address =
                                    selectedAddress
                            )
                        }
                    } else {
                        showDeliveryAddressError(
                            parseErrorMessage(
                                response
                                    .errorBody()
                                    ?.string()
                            )
                                ?: "Teslimat adresleri alınamadı."
                        )
                    }
                } catch (_: IOException) {
                    showDeliveryAddressError(
                        "Teslimat adresleri için sunucuya bağlanılamadı."
                    )
                } catch (_: Exception) {
                    showDeliveryAddressError(
                        "Teslimat adresleri yüklenirken bir hata oluştu."
                    )
                }
            }
    }

    /*
     * H8D-2C
     *
     * Keşfet içindeki hızlı adres seçici yalnız uygulamanın
     * aktif teslimat adresini değiştirir.
     *
     * Backend Address.IsDefault alanına dokunulmaz.
     *
     * Seçim başarılı olduğunda:
     * - DataStore selectedDeliveryAddressId güncellenir,
     * - Keşfet üst adres kartı anında güncellenir,
     * - Food discover page=1,
     * - Storefront discover page=1
     * yeni city/latitude/longitude ile yeniden yüklenir.
     */
    fun selectDeliveryAddress(
        addressId: Int
    ) {
        if (
            addressId <= 0 ||
            _uiState.value
                .isDeliveryAddressLoading
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                .selectedDeliveryAddress
                ?.id ==
            addressId
        ) {
            return
        }

        viewModelScope.launch {
            val selectedAddress =
                deliveryAddressSelectionManager
                    .select(
                        addressId =
                            addressId,

                        addresses =
                            _uiState.value
                                .deliveryAddresses
                    )
                    ?: return@launch

            _uiState.value =
                _uiState.value.copy(
                    selectedDeliveryAddress =
                        selectedAddress,

                    deliveryAddressErrorMessage =
                        null
                )

            loadInitialResults(
                address =
                    selectedAddress
            )
        }
    }

    fun updateSearchQuery(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                searchQuery =
                    value.take(100)
            )
    }

    fun search() {
        val address =
            _uiState.value
                .selectedDeliveryAddress
                ?: return

        loadInitialResults(
            address =
                address
        )
    }

    fun selectCategory(
        categoryId: Int?
    ) {
        if (
            _uiState.value
                .selectedCategoryId ==
            categoryId
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId =
                    categoryId
            )

        val address =
            _uiState.value
                .selectedDeliveryAddress
                ?: return

        loadInitialResults(
            address =
                address
        )
    }

    fun clearFilters() {
        _uiState.value =
            _uiState.value.copy(
                searchQuery = "",
                selectedCategoryId = null
            )

        val address =
            _uiState.value
                .selectedDeliveryAddress
                ?: return

        loadInitialResults(
            address =
                address,

            search =
                "",

            categoryId =
                null
        )
    }

    fun selectContentType(
        contentType:
        CustomerExploreContentType
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedContentType =
                    contentType
            )
    }

    fun retryDeliveryAddress() {
        refreshDeliveryContext()
    }

    fun retryCategories() {
        loadCategories()
    }

    fun retryFoods() {
        val address =
            _uiState.value
                .selectedDeliveryAddress
                ?: return

        loadFoodsPage(
            address =
                address,

            page =
                1,

            append =
                false
        )
    }

    fun retryStorefronts() {
        val address =
            _uiState.value
                .selectedDeliveryAddress
                ?: return

        loadStorefrontsPage(
            address =
                address,

            page =
                1,

            append =
                false
        )
    }

    fun loadMoreForSelectedTab() {
        when (
            _uiState.value
                .selectedContentType
        ) {
            CustomerExploreContentType
                .FOODS -> {
                loadMoreFoods()
            }

            CustomerExploreContentType
                .STOREFRONTS -> {
                loadMoreStorefronts()
            }
        }
    }

    private fun loadMoreFoods() {
        val state =
            _uiState.value

        val address =
            state.selectedDeliveryAddress
                ?: return

        if (
            state.isFoodsLoading ||
            state.isFoodsLoadingMore ||
            !state.foodHasNextPage
        ) {
            return
        }

        loadFoodsPage(
            address =
                address,

            page =
                state.foodPage + 1,

            append =
                true
        )
    }

    private fun loadMoreStorefronts() {
        val state =
            _uiState.value

        val address =
            state.selectedDeliveryAddress
                ?: return

        if (
            state.isStorefrontsLoading ||
            state.isStorefrontsLoadingMore ||
            !state.storefrontHasNextPage
        ) {
            return
        }

        loadStorefrontsPage(
            address =
                address,

            page =
                state.storefrontPage + 1,

            append =
                true
        )
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isCategoriesLoading =
                        true,

                    categoryErrorMessage =
                        null
                )

            try {
                val response =
                    categoryRepository
                        .getCategories()

                val body =
                    response.body()

                val categories =
                    body?.data

                if (
                    response.isSuccessful &&
                    body?.success == true &&
                    categories != null
                ) {
                    _uiState.value =
                        _uiState.value.copy(
                            isCategoriesLoading =
                                false,

                            categories =
                                categories.filter {
                                    it.isActive !=
                                            false
                                },

                            categoryErrorMessage =
                                null
                        )
                } else {
                    showCategoryError(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: "Kategoriler alınamadı."
                    )
                }
            } catch (_: IOException) {
                showCategoryError(
                    "Kategoriler için sunucuya bağlanılamadı."
                )
            } catch (_: Exception) {
                showCategoryError(
                    "Kategoriler yüklenirken bir hata oluştu."
                )
            }
        }
    }

    private fun loadInitialResults(
        address: AddressResponse,
        search: String =
            _uiState.value
                .searchQuery
                .trim(),
        categoryId: Int? =
            _uiState.value
                .selectedCategoryId
    ) {
        initialResultLoadJob
            ?.cancel()

        foodLoadMoreJob
            ?.cancel()

        storefrontLoadMoreJob
            ?.cancel()

        initialResultLoadJob =
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        isFoodsLoading =
                            true,

                        isFoodsLoadingMore =
                            false,

                        foods =
                            emptyList(),

                        foodPage =
                            0,

                        foodTotalCount =
                            0,

                        foodHasNextPage =
                            false,

                        foodErrorMessage =
                            null,

                        isStorefrontsLoading =
                            true,

                        isStorefrontsLoadingMore =
                            false,

                        storefronts =
                            emptyList(),

                        storefrontPage =
                            0,

                        storefrontTotalCount =
                            0,

                        storefrontHasNextPage =
                            false,

                        storefrontErrorMessage =
                            null
                    )

                coroutineScope {
                    val foodDeferred =
                        async {
                            requestFoodPage(
                                address =
                                    address,

                                page =
                                    1,

                                search =
                                    search,

                                categoryId =
                                    categoryId
                            )
                        }

                    val storefrontDeferred =
                        async {
                            requestStorefrontPage(
                                address =
                                    address,

                                page =
                                    1,

                                search =
                                    search,

                                categoryId =
                                    categoryId
                            )
                        }

                    val foodResult =
                        foodDeferred.await()

                    val storefrontResult =
                        storefrontDeferred.await()

                    applyInitialFoodResult(
                        foodResult
                    )

                    applyInitialStorefrontResult(
                        storefrontResult
                    )
                }
            }
    }

    private fun loadFoodsPage(
        address: AddressResponse,
        page: Int,
        append: Boolean
    ) {
        foodLoadMoreJob
            ?.cancel()

        foodLoadMoreJob =
            viewModelScope.launch {
                if (append) {
                    _uiState.value =
                        _uiState.value.copy(
                            isFoodsLoadingMore =
                                true,

                            foodErrorMessage =
                                null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isFoodsLoading =
                                true,

                            foods =
                                emptyList(),

                            foodPage =
                                0,

                            foodTotalCount =
                                0,

                            foodHasNextPage =
                                false,

                            foodErrorMessage =
                                null
                        )
                }

                val result =
                    requestFoodPage(
                        address =
                            address,

                        page =
                            page,

                        search =
                            _uiState.value
                                .searchQuery
                                .trim(),

                        categoryId =
                            _uiState.value
                                .selectedCategoryId
                    )

                when (result) {
                    is DiscoverPageResult.Success -> {
                        val pageData =
                            result.data

                        _uiState.value =
                            _uiState.value.copy(
                                isFoodsLoading =
                                    false,

                                isFoodsLoadingMore =
                                    false,

                                foods =
                                    if (append) {
                                        _uiState.value
                                            .foods +
                                                pageData.items
                                    } else {
                                        pageData.items
                                    },

                                foodPage =
                                    pageData.page,

                                foodTotalCount =
                                    pageData.totalCount,

                                foodHasNextPage =
                                    pageData.hasNextPage,

                                foodErrorMessage =
                                    null
                            )
                    }

                    is DiscoverPageResult.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isFoodsLoading =
                                    false,

                                isFoodsLoadingMore =
                                    false,

                                foodErrorMessage =
                                    result.message
                            )
                    }
                }
            }
    }

    private fun loadStorefrontsPage(
        address: AddressResponse,
        page: Int,
        append: Boolean
    ) {
        storefrontLoadMoreJob
            ?.cancel()

        storefrontLoadMoreJob =
            viewModelScope.launch {
                if (append) {
                    _uiState.value =
                        _uiState.value.copy(
                            isStorefrontsLoadingMore =
                                true,

                            storefrontErrorMessage =
                                null
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isStorefrontsLoading =
                                true,

                            storefronts =
                                emptyList(),

                            storefrontPage =
                                0,

                            storefrontTotalCount =
                                0,

                            storefrontHasNextPage =
                                false,

                            storefrontErrorMessage =
                                null
                        )
                }

                val result =
                    requestStorefrontPage(
                        address =
                            address,

                        page =
                            page,

                        search =
                            _uiState.value
                                .searchQuery
                                .trim(),

                        categoryId =
                            _uiState.value
                                .selectedCategoryId
                    )

                when (result) {
                    is DiscoverPageResult.Success -> {
                        val pageData =
                            result.data

                        _uiState.value =
                            _uiState.value.copy(
                                isStorefrontsLoading =
                                    false,

                                isStorefrontsLoadingMore =
                                    false,

                                storefronts =
                                    if (append) {
                                        _uiState.value
                                            .storefronts +
                                                pageData.items
                                    } else {
                                        pageData.items
                                    },

                                storefrontPage =
                                    pageData.page,

                                storefrontTotalCount =
                                    pageData.totalCount,

                                storefrontHasNextPage =
                                    pageData.hasNextPage,

                                storefrontErrorMessage =
                                    null
                            )
                    }

                    is DiscoverPageResult.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isStorefrontsLoading =
                                    false,

                                isStorefrontsLoadingMore =
                                    false,

                                storefrontErrorMessage =
                                    result.message
                            )
                    }
                }
            }
    }

    private suspend fun requestFoodPage(
        address: AddressResponse,
        page: Int,
        search: String,
        categoryId: Int?
    ): DiscoverPageResult<
            com.homemadefood.app.data.model.PagedResultResponse<
                    com.homemadefood.app.data.model.DiscoverFoodResponse>
            > {
        return try {
            val response =
                foodRepository
                    .getDiscoverFoods(
                        latitude =
                            address.latitude,

                        longitude =
                            address.longitude,

                        city =
                            address.city.trim(),

                        radiusKm =
                            DISCOVER_RADIUS_KM,

                        page =
                            page,

                        pageSize =
                            PAGE_SIZE,

                        categoryId =
                            categoryId,

                        search =
                            search.takeIf {
                                it.isNotBlank()
                            }
                    )

            val body =
                response.body()

            val data =
                body?.data

            if (
                response.isSuccessful &&
                body?.success == true &&
                data != null
            ) {
                DiscoverPageResult
                    .Success(
                        data
                    )
            } else {
                DiscoverPageResult
                    .Error(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: "Yakınındaki yemekler alınamadı."
                    )
            }
        } catch (_: IOException) {
            DiscoverPageResult
                .Error(
                    "Yakınındaki yemekler için sunucuya bağlanılamadı."
                )
        } catch (_: Exception) {
            DiscoverPageResult
                .Error(
                    "Yakınındaki yemekler yüklenirken bir hata oluştu."
                )
        }
    }

    private suspend fun requestStorefrontPage(
        address: AddressResponse,
        page: Int,
        search: String,
        categoryId: Int?
    ): DiscoverPageResult<
            com.homemadefood.app.data.model.PagedResultResponse<
                    com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse>
            > {
        return try {
            val response =
                storefrontRepository
                    .getDiscoverStorefronts(
                        latitude =
                            address.latitude,

                        longitude =
                            address.longitude,

                        city =
                            address.city.trim(),

                        radiusKm =
                            DISCOVER_RADIUS_KM,

                        page =
                            page,

                        pageSize =
                            PAGE_SIZE,

                        categoryId =
                            categoryId,

                        search =
                            search.takeIf {
                                it.isNotBlank()
                            }
                    )

            val body =
                response.body()

            val data =
                body?.data

            if (
                response.isSuccessful &&
                body?.success == true &&
                data != null
            ) {
                DiscoverPageResult
                    .Success(
                        data
                    )
            } else {
                DiscoverPageResult
                    .Error(
                        parseErrorMessage(
                            response
                                .errorBody()
                                ?.string()
                        )
                            ?: "Yakınındaki işletmeler alınamadı."
                    )
            }
        } catch (_: IOException) {
            DiscoverPageResult
                .Error(
                    "Yakınındaki işletmeler için sunucuya bağlanılamadı."
                )
        } catch (_: Exception) {
            DiscoverPageResult
                .Error(
                    "Yakınındaki işletmeler yüklenirken bir hata oluştu."
                )
        }
    }

    private fun applyInitialFoodResult(
        result:
        DiscoverPageResult<
                com.homemadefood.app.data.model.PagedResultResponse<
                        com.homemadefood.app.data.model.DiscoverFoodResponse>
                >
    ) {
        when (result) {
            is DiscoverPageResult.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        isFoodsLoading =
                            false,

                        foods =
                            result.data.items,

                        foodPage =
                            result.data.page,

                        foodTotalCount =
                            result.data.totalCount,

                        foodHasNextPage =
                            result.data.hasNextPage,

                        foodErrorMessage =
                            null
                    )
            }

            is DiscoverPageResult.Error -> {
                _uiState.value =
                    _uiState.value.copy(
                        isFoodsLoading =
                            false,

                        foods =
                            emptyList(),

                        foodErrorMessage =
                            result.message
                    )
            }
        }
    }

    private fun applyInitialStorefrontResult(
        result:
        DiscoverPageResult<
                com.homemadefood.app.data.model.PagedResultResponse<
                        com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse>
                >
    ) {
        when (result) {
            is DiscoverPageResult.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        isStorefrontsLoading =
                            false,

                        storefronts =
                            result.data.items,

                        storefrontPage =
                            result.data.page,

                        storefrontTotalCount =
                            result.data.totalCount,

                        storefrontHasNextPage =
                            result.data.hasNextPage,

                        storefrontErrorMessage =
                            null
                    )
            }

            is DiscoverPageResult.Error -> {
                _uiState.value =
                    _uiState.value.copy(
                        isStorefrontsLoading =
                            false,

                        storefronts =
                            emptyList(),

                        storefrontErrorMessage =
                            result.message
                    )
            }
        }
    }

    private fun clearDiscoverResults() {
        initialResultLoadJob
            ?.cancel()

        foodLoadMoreJob
            ?.cancel()

        storefrontLoadMoreJob
            ?.cancel()

        _uiState.value =
            _uiState.value.copy(
                isFoodsLoading =
                    false,

                isFoodsLoadingMore =
                    false,

                foods =
                    emptyList(),

                foodPage =
                    0,

                foodTotalCount =
                    0,

                foodHasNextPage =
                    false,

                foodErrorMessage =
                    null,

                isStorefrontsLoading =
                    false,

                isStorefrontsLoadingMore =
                    false,

                storefronts =
                    emptyList(),

                storefrontPage =
                    0,

                storefrontTotalCount =
                    0,

                storefrontHasNextPage =
                    false,

                storefrontErrorMessage =
                    null
            )
    }

    private fun showDeliveryAddressError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isDeliveryAddressLoading =
                    false,

                deliveryAddresses =
                    emptyList(),

                selectedDeliveryAddress =
                    null,

                deliveryAddressErrorMessage =
                    message
            )

        clearDiscoverResults()
    }

    private fun showCategoryError(
        message: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                isCategoriesLoading =
                    false,

                categoryErrorMessage =
                    message
            )
    }

    private fun parseErrorMessage(
        errorJson: String?
    ): String? {
        if (
            errorJson.isNullOrBlank()
        ) {
            return null
        }

        return runCatching {
            JSONObject(
                errorJson
            )
                .optString(
                    "message"
                )
                .takeIf {
                    it.isNotBlank()
                }
        }.getOrNull()
    }

    private sealed interface
    DiscoverPageResult<out T> {

        data class Success<T>(
            val data: T
        ) :
            DiscoverPageResult<T>

        data class Error(
            val message: String
        ) :
            DiscoverPageResult<Nothing>
    }
}