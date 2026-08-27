package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.ProducerStorefrontMenuCategoryResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuFoodResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuResponse
import com.homemadefood.app.data.remote.ApiConfig
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.FoodImage
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.PaddingValues

private const val STOREFRONT_MENU_INTRO_INDEX = 2
private const val STOREFRONT_FIRST_CATEGORY_INDEX = 4
private const val CATEGORY_KEY_PREFIX = "storefront_category_"

private val STOREFRONT_TOP_SPACE = 0.dp
private val STOREFRONT_BOTTOM_SPACE = 0.dp
@Composable
fun StorefrontMenuScreen(
    uiState: StorefrontMenuUiState,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onRetryClick: () -> Unit,
    onFoodClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = CustomerHomeColors.Cream
        ) {
            when {
                uiState.isLoading -> {
                    StorefrontLoadingContent(
                        cartTotalQuantity = cartTotalQuantity,
                        onBackClick = onBackClick,
                        onCartClick = onCartClick
                    )
                }

                uiState.errorMessage != null -> {
                    StorefrontErrorContent(
                        message = uiState.errorMessage,
                        cartTotalQuantity = cartTotalQuantity,
                        onBackClick = onBackClick,
                        onCartClick = onCartClick,
                        onRetryClick = onRetryClick
                    )
                }

                uiState.menu != null -> {
                    StorefrontLoadedContent(
                        menu = uiState.menu,
                        cartTotalQuantity = cartTotalQuantity,
                        onBackClick = onBackClick,
                        onCartClick = onCartClick,
                        onFoodClick = onFoodClick
                    )
                }
            }
        }
    }
}

@Composable
private fun StorefrontLoadingContent(
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        StorefrontSimpleTopBar(
            cartTotalQuantity = cartTotalQuantity,
            onBackClick = onBackClick,
            onCartClick = onCartClick
        )

        AppLoadingState(
            modifier = Modifier.weight(1f),
            message = "İşletme menüsü hazırlanıyor..."
        )
    }
}

@Composable
private fun StorefrontErrorContent(
    message: String,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        StorefrontSimpleTopBar(
            cartTotalQuantity = cartTotalQuantity,
            onBackClick = onBackClick,
            onCartClick = onCartClick
        )

        AppErrorState(
            message = message,
            onRetryClick = onRetryClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StorefrontLoadedContent(
    menu: ProducerStorefrontMenuResponse,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onFoodClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val categoryBarState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var categoryBarHeightPx by
    remember { mutableIntStateOf(0) }

    var selectedCategoryId by
    remember(menu.producerProfileId) {
        mutableStateOf<Int?>(null)
    }

    val visibleCategoryId by
    remember(listState, menu.categories) {
        derivedStateOf {
            resolveVisibleCategoryId(
                listState = listState,
                categoryBarHeightPx = categoryBarHeightPx
            )
        }
    }

    LaunchedEffect(visibleCategoryId) {
        selectedCategoryId = visibleCategoryId
    }

    LaunchedEffect(selectedCategoryId, menu.categories) {
        val chipIndex =
            if (selectedCategoryId == null) {
                0
            } else {
                menu.categories
                    .indexOfFirst {
                        it.categoryId == selectedCategoryId
                    }
                    .takeIf { it >= 0 }
                    ?.plus(1)
                    ?: 0
            }

        categoryBarState.animateScrollToItem(
            index = (chipIndex - 1).coerceAtLeast(0)
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream),
        contentPadding = PaddingValues(
            top = STOREFRONT_TOP_SPACE,
            bottom = STOREFRONT_BOTTOM_SPACE
        )
    ) {
        item(
            key = "storefront_hero",
            contentType = "hero"
        ) {
            StorefrontHero(
                businessName = menu.businessName,
                businessImageUrl = menu.businessImageUrl,
                cartTotalQuantity = cartTotalQuantity,
                onBackClick = onBackClick,
                onCartClick = onCartClick
            )
        }

        item(
            key = "storefront_info",
            contentType = "storefront_info"
        ) {
            StorefrontBusinessInfo(
                businessName = menu.businessName,
                description = menu.description,
                rating = menu.rating,
                city = menu.city,
                district = menu.district,
                availableFoodCount = menu.availableFoodCount,
                availableCategoryCount = menu.availableCategoryCount
            )
        }

        item(
            key = "storefront_menu_intro",
            contentType = "menu_intro"
        ) {
            StorefrontMenuIntro()
        }

        stickyHeader(
            key = "storefront_category_bar",
            contentType = "category_bar"
        ) {
            StorefrontCategoryBar(
                categories = menu.categories,
                selectedCategoryId = selectedCategoryId,
                listState = categoryBarState,
                modifier = Modifier.onSizeChanged {
                    categoryBarHeightPx = it.height
                },
                onAllClick = {
                    selectedCategoryId = null

                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            index = STOREFRONT_MENU_INTRO_INDEX
                        )
                    }
                },
                onCategoryClick = { categoryId ->
                    val categoryIndex =
                        menu.categories.indexOfFirst {
                            it.categoryId == categoryId
                        }

                    if (categoryIndex >= 0) {
                        selectedCategoryId = categoryId

                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index =
                                    STOREFRONT_FIRST_CATEGORY_INDEX +
                                            categoryIndex,
                                scrollOffset =
                                    -categoryBarHeightPx
                                        .coerceAtLeast(1)
                            )
                        }
                    }
                }
            )
        }

        if (menu.categories.isEmpty()) {
            item(
                key = "storefront_empty_menu",
                contentType = "empty_menu"
            ) {
                StorefrontEmptyMenu()
            }
        } else {
            menu.categories.forEachIndexed {
                    index,
                    category ->

                item(
                    key = "$CATEGORY_KEY_PREFIX${category.categoryId}",
                    contentType = "category_section"
                ) {
                    StorefrontMenuCategorySection(
                        category = category,
                        showDivider =
                            index < menu.categories.lastIndex,
                        onFoodClick = onFoodClick
                    )
                }
            }
        }


    }
}

@Composable
private fun StorefrontSimpleTopBar(
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StorefrontBackButton(
            onClick = onBackClick
        )

        CustomerHomeCartButton(
            totalQuantity = cartTotalQuantity,
            onClick = onCartClick
        )
    }
}

@Composable
private fun StorefrontHero(
    businessName: String,
    businessImageUrl: String?,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val resolvedBusinessImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(188.dp)
    ) {
        if (resolvedBusinessImageUrl == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                CustomerHomeColors.OliveSoft,
                                CustomerHomeColors.SurfaceSoft
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = businessName.take(1).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            AsyncImage(
                model = resolvedBusinessImageUrl,
                contentDescription =
                    "$businessName işletme kapak görseli",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.12f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorefrontBackButton(
                onClick = onBackClick,
                overlay = true
            )

            CustomerHomeCartButton(
                totalQuantity = cartTotalQuantity,
                onClick = onCartClick
            )
        }
    }
}

@Composable
private fun StorefrontBackButton(
    onClick: () -> Unit,
    overlay: Boolean = false
) {
    Surface(
        modifier = Modifier
            .size(46.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color =
            if (overlay) {
                Color.White.copy(alpha = 0.94f)
            } else {
                CustomerHomeColors.Surface
            },
        shadowElevation = 4.dp,
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline.copy(alpha = 0.70f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "←",
                color = CustomerHomeColors.DeepOlive,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StorefrontBusinessInfo(
    businessName: String,
    description: String,
    rating: Double,
    city: String,
    district: String,
    availableFoodCount: Int,
    availableCategoryCount: Int
) {
    val location =
        listOf(
            district,
            city
        )
            .filter { it.isNotBlank() }
            .joinToString(" / ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 8.dp
            )
    ) {
        Text(
            text = businessName,
            style = MaterialTheme.typography.headlineSmall,
            color = CustomerHomeColors.Text,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =
                    if (rating > 0.0) {
                        "★ ${formatRating(rating)}"
                    } else {
                        "Yeni işletme"
                    },
                style = MaterialTheme.typography.labelLarge,
                color =
                    if (rating > 0.0) {
                        CustomerHomeColors.Gold
                    } else {
                        CustomerHomeColors.Olive
                    },
                fontWeight = FontWeight.SemiBold
            )

            if (location.isNotBlank()) {
                Text(
                    text = "  •  ",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = location,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomerHomeColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (description.isNotBlank()) {
            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 21.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StorefrontInfoPill(
                text = "$availableFoodCount yemek",
                backgroundColor = CustomerHomeColors.OliveSoft,
                contentColor = CustomerHomeColors.DeepOlive
            )

            StorefrontInfoPill(
                text = "$availableCategoryCount kategori",
                backgroundColor = CustomerHomeColors.TerracottaSoft,
                contentColor = CustomerHomeColors.Terracotta
            )
        }
    }
}

@Composable
private fun StorefrontInfoPill(
    text: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StorefrontMenuIntro() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 22.dp,
                bottom = 14.dp
            )
    ) {
        Text(
            text = "MENÜ",
            style = MaterialTheme.typography.labelLarge,
            color = CustomerHomeColors.Terracotta,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Bugün neler var?",
            style = MaterialTheme.typography.headlineSmall,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "Kategoriler arasında kaydırın veya aşağıdaki menüden seçin.",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )
    }
}

@Composable
private fun StorefrontCategoryBar(
    categories: List<ProducerStorefrontMenuCategoryResponse>,
    selectedCategoryId: Int?,
    listState: LazyListState,
    onAllClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CustomerHomeColors.Cream,
        shadowElevation = 3.dp
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(
                key = "all_categories_chip"
            ) {
                StorefrontCategoryChip(
                    text = "Tümü",
                    selected = selectedCategoryId == null,
                    onClick = onAllClick
                )
            }

            itemsIndexed(
                items = categories,
                key = { _, category ->
                    category.categoryId
                }
            ) {
                    _,
                    category ->

                StorefrontCategoryChip(
                    text = category.categoryName,
                    selected =
                        selectedCategoryId ==
                                category.categoryId,
                    onClick = {
                        onCategoryClick(
                            category.categoryId
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StorefrontCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(
            onClick = onClick
        ),
        shape = RoundedCornerShape(50.dp),
        color =
            if (selected) {
                CustomerHomeColors.DeepOlive
            } else {
                CustomerHomeColors.Surface
            },
        contentColor =
            if (selected) {
                Color.White
            } else {
                CustomerHomeColors.DeepOlive
            },
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    width = 1.dp,
                    color = CustomerHomeColors.Outline
                )
            },
        shadowElevation =
            if (selected) {
                1.dp
            } else {
                0.dp
            }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 9.dp
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun StorefrontMenuCategorySection(
    category: ProducerStorefrontMenuCategoryResponse,
    showDivider: Boolean,
    onFoodClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = category.categoryName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${category.foods.size} yemek",
                style = MaterialTheme.typography.labelMedium,
                color = CustomerHomeColors.TextMuted
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        category.foods.forEachIndexed {
                index,
                food ->

            StorefrontMenuFoodCard(
                food = food,
                onClick = {
                    onFoodClick(food.id)
                }
            )

            if (index < category.foods.lastIndex) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }

        if (showDivider) {
            Spacer(
                modifier = Modifier.height(22.dp)
            )

            HorizontalDivider(
                color = CustomerHomeColors.Outline.copy(alpha = 0.72f)
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )
    }
}

@Composable
private fun StorefrontMenuFoodCard(
    food: ProducerStorefrontMenuFoodResponse,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = CustomerHomeColors.Outline.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FoodImage(
                imageUrl = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier
                    .width(104.dp)
                    .height(104.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomerHomeColors.Text,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (food.description.isNotBlank()) {
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = food.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = CustomerHomeColors.TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(food.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = CustomerHomeColors.Terracotta,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = CustomerHomeColors.SurfaceSoft
                    ) {
                        Text(
                            text = "${food.preparationTimeMinutes} dk",
                            modifier = Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = CustomerHomeColors.Olive,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = "›",
                color = CustomerHomeColors.DeepOlive,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StorefrontEmptyMenu() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 48.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Menü henüz hazır değil",
            style = MaterialTheme.typography.titleMedium,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = "Bu işletmenin şu anda satışta olan bir yemeği bulunmuyor.",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomerHomeColors.TextMuted
        )
    }
}

private fun resolveVisibleCategoryId(
    listState: LazyListState,
    categoryBarHeightPx: Int
): Int? {
    val activationLine =
        categoryBarHeightPx.coerceAtLeast(1)

    return listState.layoutInfo.visibleItemsInfo
        .mapNotNull { itemInfo ->
            val key = itemInfo.key as? String
                ?: return@mapNotNull null

            if (!key.startsWith(CATEGORY_KEY_PREFIX)) {
                return@mapNotNull null
            }

            val categoryId =
                key.removePrefix(
                    CATEGORY_KEY_PREFIX
                )
                    .toIntOrNull()
                    ?: return@mapNotNull null

            Triple(
                categoryId,
                itemInfo.offset,
                itemInfo.index
            )
        }
        .filter { (_, offset, _) ->
            offset <= activationLine
        }
        .maxByOrNull { (_, _, index) ->
            index
        }
        ?.first
}

private fun formatPrice(
    price: Double
): String {
    val locale = Locale("tr", "TR")

    return if (price % 1.0 == 0.0) {
        String.format(
            locale,
            "%.0f ₺",
            price
        )
    } else {
        String.format(
            locale,
            "%.2f ₺",
            price
        )
    }
}

private fun formatRating(
    rating: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.1f",
        rating
    )
}