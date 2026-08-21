package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.R
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.CategoryResponse
import com.homemadefood.app.data.model.DiscoverFoodResponse
import com.homemadefood.app.data.model.DiscoverProducerStorefrontResponse
import com.homemadefood.app.data.remote.ApiConfig
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CustomerExploreScreen(
    uiState: CustomerExploreUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onRecommendationClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
    onClearFiltersClick: () -> Unit,
    onContentTypeClick:
        (CustomerExploreContentType) -> Unit,
    onRetryDeliveryAddressClick: () -> Unit,
    onDeliveryAddressSelected: (Int) -> Unit,
    onRetryCategoriesClick: () -> Unit,
    onRetryFoodsClick: () -> Unit,
    onRetryStorefrontsClick: () -> Unit,
    onLoadMoreClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    onFoodClick: (Int) -> Unit,
    onStorefrontClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        var isDeliveryAddressSheetVisible by
        rememberSaveable {
            mutableStateOf(
                false
            )
        }

        if (
            isDeliveryAddressSheetVisible
        ) {
            CustomerExploreDeliveryAddressSheet(
                addresses =
                    uiState
                        .deliveryAddresses,

                selectedAddressId =
                    uiState
                        .selectedDeliveryAddress
                        ?.id,

                onDismiss = {
                    isDeliveryAddressSheetVisible =
                        false
                },

                onAddressSelected = {
                        addressId ->

                    isDeliveryAddressSheetVisible =
                        false

                    onDeliveryAddressSelected(
                        addressId
                    )
                },

                onAddAddressClick = {
                    isDeliveryAddressSheetVisible =
                        false

                    onAddAddressClick()
                },

                onManageAddressesClick = {
                    isDeliveryAddressSheetVisible =
                        false

                    onManageAddressesClick()
                }
            )
        }

        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(
                        CustomerHomeColors
                            .Cream
                    )
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 18.dp
                        )
            ) {
                Text(
                    text = "Keşfet",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )

                Text(
                    text =
                        "Teslimat adresinin 30 km çevresindeki ev mutfaklarını keşfet",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )

                ExploreDeliveryAddressStrip(
                    isLoading =
                        uiState
                            .isDeliveryAddressLoading,

                    selectedAddress =
                        uiState
                            .selectedDeliveryAddress,

                    errorMessage =
                        uiState
                            .deliveryAddressErrorMessage,

                    onRetryClick =
                        onRetryDeliveryAddressClick,

                    onAddAddressClick =
                        onAddAddressClick,

                    onChangeAddressClick = {
                        isDeliveryAddressSheetVisible =
                            true
                    }
                )

                if (
                    uiState.selectedDeliveryAddress !=
                    null
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )

                    ExploreSearchField(
                        searchQuery =
                            uiState.searchQuery,

                        isLoading =
                            uiState.isFoodsLoading ||
                                    uiState
                                        .isStorefrontsLoading,

                        onSearchQueryChange =
                            onSearchQueryChange,

                        onSearchClick =
                            onSearchClick,

                        onRecommendationClick =
                            onRecommendationClick
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )

                    ExploreCategories(
                        categories =
                            uiState.categories,

                        selectedCategoryId =
                            uiState
                                .selectedCategoryId,

                        isLoading =
                            uiState
                                .isCategoriesLoading,

                        errorMessage =
                            uiState
                                .categoryErrorMessage,

                        onCategoryClick =
                            onCategoryClick,

                        onRetryClick =
                            onRetryCategoriesClick
                    )

                    if (
                        uiState.searchQuery
                            .isNotBlank() ||
                        uiState.selectedCategoryId !=
                        null
                    ) {
                        TextButton(
                            onClick =
                                onClearFiltersClick
                        ) {
                            Text(
                                text =
                                    "Filtreleri Temizle",
                                color =
                                    CustomerHomeColors
                                        .Terracotta
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )

                    ExploreContentTabs(
                        selectedContentType =
                            uiState
                                .selectedContentType,

                        foodCount =
                            uiState
                                .foodTotalCount,

                        storefrontCount =
                            uiState
                                .storefrontTotalCount,

                        onContentTypeClick =
                            onContentTypeClick
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )
                }
            }

            if (
                uiState.selectedDeliveryAddress ==
                null
            ) {
                if (
                    !uiState
                        .isDeliveryAddressLoading
                ) {
                    ExploreAddressRequiredBody(
                        onAddAddressClick =
                            onAddAddressClick
                    )
                }

                return@Column
            }

            when (
                uiState.selectedContentType
            ) {
                CustomerExploreContentType
                    .FOODS -> {

                    ExploreFoodResults(
                        foods =
                            uiState.foods,

                        isLoading =
                            uiState
                                .isFoodsLoading,

                        isLoadingMore =
                            uiState
                                .isFoodsLoadingMore,

                        hasNextPage =
                            uiState
                                .foodHasNextPage,

                        errorMessage =
                            uiState
                                .foodErrorMessage,

                        onRetryClick =
                            onRetryFoodsClick,

                        onLoadMore =
                            onLoadMoreClick,

                        onFoodClick =
                            onFoodClick
                    )
                }

                CustomerExploreContentType
                    .STOREFRONTS -> {

                    ExploreStorefrontResults(
                        storefronts =
                            uiState
                                .storefronts,

                        isLoading =
                            uiState
                                .isStorefrontsLoading,

                        isLoadingMore =
                            uiState
                                .isStorefrontsLoadingMore,

                        hasNextPage =
                            uiState
                                .storefrontHasNextPage,

                        errorMessage =
                            uiState
                                .storefrontErrorMessage,

                        categoryFiltered =
                            uiState
                                .selectedCategoryId !=
                                    null,

                        onRetryClick =
                            onRetryStorefrontsClick,

                        onLoadMore =
                            onLoadMoreClick,

                        onStorefrontClick =
                            onStorefrontClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreDeliveryAddressStrip(
    isLoading: Boolean,
    selectedAddress: AddressResponse?,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onChangeAddressClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            ),
        color =
            CustomerHomeColors
                .SurfaceSoft,
        border =
            BorderStroke(
                1.dp,
                CustomerHomeColors
                    .Outline
            )
    ) {
        when {
            isLoading &&
                    selectedAddress == null -> {

                Row(
                    modifier =
                        Modifier.padding(
                            12.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                18.dp
                            ),
                        strokeWidth =
                            2.dp,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )

                    Text(
                        text =
                            "Teslimat adresi yükleniyor...",
                        modifier =
                            Modifier.padding(
                                start = 10.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            CustomerHomeColors
                                .TextMuted
                    )
                }
            }

            selectedAddress != null -> {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal =
                                    12.dp,
                                vertical =
                                    10.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )

                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {
                        Text(
                            text =
                                selectedAddress
                                    .title,
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            maxLines =
                                1,
                            overflow =
                                TextOverflow
                                    .Ellipsis
                        )

                        Text(
                            text =
                                exploreAddressSummary(
                                    selectedAddress
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                CustomerHomeColors
                                    .TextMuted,
                            maxLines =
                                1,
                            overflow =
                                TextOverflow
                                    .Ellipsis
                        )
                    }

                    TextButton(
                        onClick =
                            onChangeAddressClick
                    ) {
                        Text(
                            text =
                                "Değiştir",
                            color =
                                CustomerHomeColors
                                    .Terracotta,
                            fontWeight =
                                FontWeight
                                    .SemiBold
                        )
                    }
                }
            }

            errorMessage != null -> {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                12.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            errorMessage,
                        modifier =
                            Modifier.weight(
                                1f
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    TextButton(
                        onClick =
                            onRetryClick
                    ) {
                        Text(
                            "Tekrar Dene"
                        )
                    }
                }
            }

            else -> {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                12.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            "Keşfet için teslimat adresi gerekli.",
                        modifier =
                            Modifier.weight(
                                1f
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            CustomerHomeColors
                                .TextMuted
                    )

                    TextButton(
                        onClick =
                            onAddAddressClick
                    ) {
                        Text(
                            text =
                                "Adres Ekle",
                            color =
                                CustomerHomeColors
                                    .Terracotta
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreAddressRequiredBody(
    onAddAddressClick: () -> Unit
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            shape =
                RoundedCornerShape(
                    22.dp
                ),
            color =
                CustomerHomeColors
                    .Surface
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        22.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📍",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )

                Text(
                    text =
                        "Yakınındaki lezzetleri gösterelim",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )

                Text(
                    text =
                        "Keşfet, yalnız teslimat adresinin 30 km çevresindeki yemek ve işletmeleri gösterir.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

                Surface(
                    modifier =
                        Modifier.clickable(
                            onClick =
                                onAddAddressClick
                        ),
                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),
                    color =
                        CustomerHomeColors
                            .DeepOlive
                ) {
                    Text(
                        text =
                            "Teslimat Adresi Ekle",
                        modifier =
                            Modifier.padding(
                                horizontal =
                                    18.dp,
                                vertical =
                                    10.dp
                            ),
                        color =
                            CustomerHomeColors
                                .Cream,
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreSearchField(
    searchQuery: String,
    isLoading: Boolean,
    onSearchQueryChange:
        (String) -> Unit,
    onSearchClick: () -> Unit,
    onRecommendationClick: () -> Unit
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Surface(
            modifier =
                Modifier
                    .weight(1f)
                    .height(
                        50.dp
                    ),
            shape =
                RoundedCornerShape(
                    20.dp
                ),
            color =
                CustomerHomeColors
                    .Surface,
            border =
                BorderStroke(
                    1.dp,
                    CustomerHomeColors
                        .Outline
                )
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 14.dp,
                            end = 7.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_customer_home_search
                        ),
                    contentDescription =
                        null,
                    tint =
                        CustomerHomeColors
                            .TextMuted,
                    modifier =
                        Modifier.size(
                            19.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            10.dp
                        )
                )

                BasicTextField(
                    value =
                        searchQuery,

                    onValueChange =
                        onSearchQueryChange,

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    singleLine =
                        true,

                    textStyle =
                        MaterialTheme
                            .typography
                            .bodyMedium
                            .merge(
                                TextStyle(
                                    color =
                                        CustomerHomeColors
                                            .Text
                                )
                            ),

                    cursorBrush =
                        SolidColor(
                            CustomerHomeColors
                                .DeepOlive
                        ),

                    keyboardOptions =
                        KeyboardOptions(
                            imeAction =
                                ImeAction.Search
                        ),

                    keyboardActions =
                        KeyboardActions(
                            onSearch = {
                                onSearchClick()
                            }
                        ),

                    decorationBox = {
                            innerTextField ->

                        if (
                            searchQuery
                                .isBlank()
                        ) {
                            Text(
                                text =
                                    "Yemek veya işletme ara",
                                maxLines =
                                    1,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )
                        }

                        innerTextField()
                    }
                )

                Surface(
                    modifier =
                        Modifier.size(
                            34.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        if (isLoading) {
                            CustomerHomeColors
                                .Terracotta
                                .copy(
                                    alpha =
                                        0.50f
                                )
                        } else {
                            CustomerHomeColors
                                .Terracotta
                        }
                ) {
                    IconButton(
                        onClick =
                            onSearchClick,

                        enabled =
                            !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(
                                        15.dp
                                    ),
                                strokeWidth =
                                    2.dp,
                                color =
                                    Color.White
                            )
                        } else {
                            Icon(
                                painter =
                                    painterResource(
                                        id =
                                            R.drawable
                                                .ic_customer_home_search
                                    ),
                                contentDescription =
                                    "Ara",
                                tint =
                                    Color.White,
                                modifier =
                                    Modifier.size(
                                        17.dp
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )

        /*
         * H8D-3
         *
         * Akıllı öneri bir ana navigation destination olmadığı için
         * bottom bar'a eklenmez. Keşfet'in "kendim arayayım / bana öner"
         * karar noktasında sürekli erişilebilir hızlı aksiyon olarak durur.
         */
        Surface(
            modifier =
                Modifier
                    .size(
                        50.dp
                    )
                    .clickable(
                        onClick =
                            onRecommendationClick
                    ),
            shape =
                CircleShape,
            color =
                CustomerHomeColors
                    .DeepOlive,
            shadowElevation =
                2.dp
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_customer_ai_sparkle
                        ),
                    contentDescription =
                        "Akıllı Öneri",
                    tint =
                        CustomerHomeColors
                            .Gold,
                    modifier =
                        Modifier.size(
                            23.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun ExploreCategories(
    categories:
    List<CategoryResponse>,
    selectedCategoryId: Int?,
    isLoading: Boolean,
    errorMessage: String?,
    onCategoryClick: (Int?) -> Unit,
    onRetryClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text =
                    "Kategoriler",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    CustomerHomeColors
                        .DeepOlive
            )

            if (isLoading) {
                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(
                            16.dp
                        ),
                    strokeWidth =
                        2.dp,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                )
            }
        }

        if (
            !errorMessage
                .isNullOrBlank()
        ) {
            TextButton(
                onClick =
                    onRetryClick
            ) {
                Text(
                    text =
                        "Kategorileri Tekrar Yükle"
                )
            }

            return
        }

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {
            ExploreCategoryChip(
                text = "Tümü",
                selected =
                    selectedCategoryId ==
                            null,
                onClick = {
                    onCategoryClick(
                        null
                    )
                }
            )

            categories.forEach {
                    category ->

                ExploreCategoryChip(
                    text =
                        category.name,

                    selected =
                        selectedCategoryId ==
                                category.id,

                    onClick = {
                        onCategoryClick(
                            category.id
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ExploreCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.clickable(
                onClick =
                    onClick
            ),
        shape =
            RoundedCornerShape(
                50.dp
            ),
        color =
            if (selected) {
                CustomerHomeColors
                    .DeepOlive
            } else {
                CustomerHomeColors
                    .Surface
            },
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    1.dp,
                    CustomerHomeColors
                        .Outline
                )
            }
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal =
                        14.dp,
                    vertical =
                        8.dp
                ),
            style =
                MaterialTheme
                    .typography
                    .labelLarge,
            color =
                if (selected) {
                    CustomerHomeColors
                        .Cream
                } else {
                    CustomerHomeColors
                        .DeepOlive
                },
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun ExploreContentTabs(
    selectedContentType:
    CustomerExploreContentType,
    foodCount: Int,
    storefrontCount: Int,
    onContentTypeClick:
        (CustomerExploreContentType) -> Unit
) {
    Surface(
        shape =
            RoundedCornerShape(
                16.dp
            ),
        color =
            CustomerHomeColors
                .SurfaceSoft
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        4.dp
                    )
        ) {
            ExploreTab(
                text =
                    "Yemekler ($foodCount)",
                selected =
                    selectedContentType ==
                            CustomerExploreContentType
                                .FOODS,
                onClick = {
                    onContentTypeClick(
                        CustomerExploreContentType
                            .FOODS
                    )
                },
                modifier =
                    Modifier.weight(
                        1f
                    )
            )

            ExploreTab(
                text =
                    "İşletmeler ($storefrontCount)",
                selected =
                    selectedContentType ==
                            CustomerExploreContentType
                                .STOREFRONTS,
                onClick = {
                    onContentTypeClick(
                        CustomerExploreContentType
                            .STOREFRONTS
                    )
                },
                modifier =
                    Modifier.weight(
                        1f
                    )
            )
        }
    }
}

@Composable
private fun ExploreTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier =
        Modifier
) {
    Surface(
        modifier =
            modifier
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                13.dp
            ),
        color =
            if (selected) {
                CustomerHomeColors
                    .Surface
            } else {
                Color.Transparent
            },
        shadowElevation =
            if (selected) {
                1.dp
            } else {
                0.dp
            }
    ) {
        Box(
            modifier =
                Modifier.padding(
                    vertical =
                        10.dp
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Text(
                text = text,
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    if (selected) {
                        CustomerHomeColors
                            .DeepOlive
                    } else {
                        CustomerHomeColors
                            .TextMuted
                    }
            )
        }
    }
}

@Composable
private fun ExploreFoodResults(
    foods:
    List<DiscoverFoodResponse>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasNextPage: Boolean,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onLoadMore: () -> Unit,
    onFoodClick: (Int) -> Unit
) {
    val listState =
        rememberLazyListState()

    val shouldLoadMore by
    remember(
        listState,
        foods.size,
        hasNextPage,
        isLoadingMore
    ) {
        derivedStateOf {
            val layoutInfo =
                listState
                    .layoutInfo

            val lastVisibleIndex =
                layoutInfo
                    .visibleItemsInfo
                    .lastOrNull()
                    ?.index
                    ?: -1

            hasNextPage &&
                    !isLoadingMore &&
                    foods.isNotEmpty() &&
                    lastVisibleIndex >=
                    foods.lastIndex -
                    3
        }
    }

    LaunchedEffect(
        shouldLoadMore
    ) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    when {
        isLoading &&
                foods.isEmpty() -> {
            ExploreLoading()
        }

        errorMessage != null &&
                foods.isEmpty() -> {
            ExploreError(
                message =
                    errorMessage,
                onRetryClick =
                    onRetryClick
            )
        }

        foods.isEmpty() -> {
            ExploreEmpty(
                text =
                    "Seçili adresin 30 km çevresinde bu filtrelere uygun yemek bulunamadı."
            )
        }

        else -> {
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),
                state =
                    listState,
                contentPadding =
                    PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 18.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                items(
                    items =
                        foods,
                    key = {
                            food ->
                        food.id
                    }
                ) { food ->
                    ExploreFoodCard(
                        food =
                            food,
                        onClick = {
                            onFoodClick(
                                food.id
                            )
                        }
                    )
                }

                if (isLoadingMore) {
                    item(
                        key =
                            "food_load_more"
                    ) {
                        ExploreLoadMoreIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreStorefrontResults(
    storefronts:
    List<DiscoverProducerStorefrontResponse>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasNextPage: Boolean,
    errorMessage: String?,
    categoryFiltered: Boolean,
    onRetryClick: () -> Unit,
    onLoadMore: () -> Unit,
    onStorefrontClick: (Int) -> Unit
) {
    val listState =
        rememberLazyListState()

    val shouldLoadMore by
    remember(
        listState,
        storefronts.size,
        hasNextPage,
        isLoadingMore
    ) {
        derivedStateOf {
            val layoutInfo =
                listState
                    .layoutInfo

            val lastVisibleIndex =
                layoutInfo
                    .visibleItemsInfo
                    .lastOrNull()
                    ?.index
                    ?: -1

            hasNextPage &&
                    !isLoadingMore &&
                    storefronts.isNotEmpty() &&
                    lastVisibleIndex >=
                    storefronts.lastIndex -
                    3
        }
    }

    LaunchedEffect(
        shouldLoadMore
    ) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    when {
        isLoading &&
                storefronts.isEmpty() -> {
            ExploreLoading()
        }

        errorMessage != null &&
                storefronts.isEmpty() -> {
            ExploreError(
                message =
                    errorMessage,
                onRetryClick =
                    onRetryClick
            )
        }

        storefronts.isEmpty() -> {
            ExploreEmpty(
                text =
                    "Seçili adresin 30 km çevresinde bu filtrelere uygun işletme bulunamadı."
            )
        }

        else -> {
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),
                state =
                    listState,
                contentPadding =
                    PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 18.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                items(
                    items =
                        storefronts,
                    key = {
                            storefront ->
                        storefront
                            .producerProfileId
                    }
                ) {
                        storefront ->

                    ExploreStorefrontCard(
                        storefront =
                            storefront,
                        categoryFiltered =
                            categoryFiltered,
                        onClick = {
                            onStorefrontClick(
                                storefront
                                    .producerProfileId
                            )
                        }
                    )
                }

                if (isLoadingMore) {
                    item(
                        key =
                            "storefront_load_more"
                    ) {
                        ExploreLoadMoreIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreFoodCard(
    food: DiscoverFoodResponse,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        CustomerHomeColors
                            .Surface
                ),
        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        1.dp
                )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    9.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            ExploreImage(
                imageUrl =
                    food.imageUrl,
                contentDescription =
                    "${food.name} yemek görseli"
            )

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {
                Text(
                    text =
                        food.name,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .Text,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow
                            .Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )

                Text(
                    text =
                        food.businessName,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow
                            .Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            "${formatDistance(food.distanceKm)} • ${food.preparationTimeMinutes} dk",
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )

                    Text(
                        text =
                            formatExplorePrice(
                                food.price
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            CustomerHomeColors
                                .Terracotta
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )

            Text(
                text = "›",
                color =
                    CustomerHomeColors
                        .Terracotta,
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )
        }
    }
}

@Composable
private fun ExploreStorefrontCard(
    storefront:
    DiscoverProducerStorefrontResponse,
    categoryFiltered: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        CustomerHomeColors
                            .Surface
                ),
        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        1.dp
                )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    9.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            ExploreImage(
                imageUrl =
                    storefront
                        .businessImageUrl,
                contentDescription =
                    "${storefront.businessName} işletme görseli"
            )

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text =
                            storefront
                                .businessName,
                        modifier =
                            Modifier.weight(
                                1f
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            CustomerHomeColors
                                .Text,
                        maxLines =
                            1,
                        overflow =
                            TextOverflow
                                .Ellipsis
                    )

                    Text(
                        text =
                            if (
                                storefront.rating >
                                0.0
                            ) {
                                "★ ${
                                    String.format(
                                        Locale(
                                            "tr",
                                            "TR"
                                        ),
                                        "%.1f",
                                        storefront
                                            .rating
                                    )
                                }"
                            } else {
                                "Yeni"
                            },
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            CustomerHomeColors
                                .Gold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )

                Text(
                    text =
                        listOf(
                            storefront
                                .district,
                            storefront.city
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .joinToString(
                                " / "
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow
                            .Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text =
                            if (
                                categoryFiltered
                            ) {
                                "${storefront.matchingFoodCount} yemek"
                            } else {
                                "${storefront.availableFoodCount} yemek"
                            },
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                    )

                    Text(
                        text =
                            formatDistance(
                                storefront
                                    .distanceKm
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        fontWeight =
                            FontWeight
                                .SemiBold,
                        color =
                            CustomerHomeColors
                                .Terracotta
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )

            Text(
                text = "›",
                color =
                    CustomerHomeColors
                        .Terracotta,
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )
        }
    }
}

@Composable
private fun ExploreImage(
    imageUrl: String?,
    contentDescription: String
) {
    val resolvedUrl =
        ApiConfig.resolveMediaUrl(
            imageUrl
        )

    Surface(
        modifier =
            Modifier.size(
                88.dp
            ),
        shape =
            RoundedCornerShape(
                15.dp
            ),
        color =
            CustomerHomeColors
                .SurfaceSoft
    ) {
        if (resolvedUrl == null) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "🍽️",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium
                )
            }
        } else {
            AsyncImage(
                model =
                    resolvedUrl,
                contentDescription =
                    contentDescription,
                contentScale =
                    ContentScale.Crop,
                modifier =
                    Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ExploreLoading() {
    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        CircularProgressIndicator(
            color =
                CustomerHomeColors
                    .DeepOlive
        )
    }
}

@Composable
private fun ExploreLoadMoreIndicator() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    14.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier.size(
                    22.dp
                ),
            strokeWidth =
                2.dp,
            color =
                CustomerHomeColors
                    .DeepOlive
        )
    }
}

@Composable
private fun ExploreEmpty(
    text: String
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {
        Text(
            text = text,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                CustomerHomeColors
                    .TextMuted
        )
    }
}

@Composable
private fun ExploreError(
    message: String,
    onRetryClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal =
                        18.dp
                ),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        color =
            CustomerHomeColors
                .SurfaceSoft
    ) {
        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {
            Text(
                text =
                    message,
                color =
                    MaterialTheme
                        .colorScheme
                        .error,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            TextButton(
                onClick =
                    onRetryClick
            ) {
                Text(
                    text =
                        "Tekrar Dene"
                )
            }
        }
    }
}

private fun exploreAddressSummary(
    address: AddressResponse
): String {
    val summary =
        listOf(
            address.district,
            address.city
        )
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .joinToString(
                " / "
            )

    return summary
        .ifBlank {
            address.fullAddress
        }
}

private fun formatDistance(
    distanceKm: Double
): String {
    return String.format(
        Locale(
            "tr",
            "TR"
        ),
        "%.1f km",
        distanceKm
    )
}

private fun formatExplorePrice(
    price: Double
): String {
    val formatter =
        NumberFormat
            .getNumberInstance(
                Locale(
                    "tr",
                    "TR"
                )
            )

    formatter
        .minimumFractionDigits =
        0

    formatter
        .maximumFractionDigits =
        2

    return "${
        formatter.format(
            price
        )
    } ₺"
}