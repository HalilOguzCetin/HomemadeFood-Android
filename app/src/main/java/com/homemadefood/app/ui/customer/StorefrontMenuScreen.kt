package com.homemadefood.app.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.model.ProducerStorefrontMenuCategoryResponse
import com.homemadefood.app.data.model.ProducerStorefrontMenuFoodResponse
import com.homemadefood.app.data.remote.ApiConfig
import com.homemadefood.app.ui.components.CustomerCartButton
import com.homemadefood.app.ui.components.FoodImage
import java.util.Locale

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
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBackClick
            ) {
                Text("← Geri")
            }

            CustomerCartButton(
                totalQuantity =
                    cartTotalQuantity,

                onClick =
                    onCartClick
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment
                                .CenterHorizontally
                        )
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text =
                        uiState.errorMessage,

                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Button(
                    onClick = onRetryClick,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }

            uiState.menu != null -> {
                val menu =
                    uiState.menu

                StorefrontHeader(
                    businessName =
                        menu.businessName,

                    description =
                        menu.description,

                    businessImageUrl =
                        menu.businessImageUrl,

                    rating =
                        menu.rating,

                    city =
                        menu.city,

                    district =
                        menu.district,

                    availableFoodCount =
                        menu.availableFoodCount,

                    availableCategoryCount =
                        menu.availableCategoryCount
                )

                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )

                Text(
                    text = "Menü",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Yemekler kategorilerine göre listelenmiştir.",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                menu.categories
                    .forEachIndexed {
                            index,
                            category ->

                        StorefrontMenuCategorySection(
                            category =
                                category,

                            onFoodClick =
                                onFoodClick
                        )

                        if (
                            index <
                            menu.categories.lastIndex
                        ) {
                            Spacer(
                                modifier =
                                    Modifier.height(
                                        24.dp
                                    )
                            )

                            HorizontalDivider()

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        24.dp
                                    )
                            )
                        }
                    }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StorefrontHeader(
    businessName: String,
    description: String,
    businessImageUrl: String?,
    rating: Double,
    city: String,
    district: String,
    availableFoodCount: Int,
    availableCategoryCount: Int
) {
    val resolvedBusinessImageUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {
        Column {
            if (
                resolvedBusinessImageUrl ==
                null
            ) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp),

                    tonalElevation = 1.dp
                ) {
                    Box(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text =
                                "İşletme görseli bulunmuyor"
                        )
                    }
                }
            } else {
                AsyncImage(
                    model =
                        resolvedBusinessImageUrl,

                    contentDescription =
                        "$businessName işletme vitrin görseli",

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp),

                    contentScale =
                        ContentScale.Crop
                )
            }

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {
                Text(
                    text =
                        businessName,

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
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
                            if (rating > 0.0) {
                                "★ ${
                                    formatRating(
                                        rating
                                    )
                                }"
                            } else {
                                "Yeni işletme"
                            },

                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )

                    val location =
                        listOf(
                            district,
                            city
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .joinToString(
                                " / "
                            )

                    if (location.isNotBlank()) {
                        Text(
                            text =
                                location,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }

                if (
                    description.isNotBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            description,

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {
                    StorefrontInfoBadge(
                        text =
                            "$availableFoodCount aktif yemek",

                        modifier =
                            Modifier.weight(1f)
                    )

                    StorefrontInfoBadge(
                        text =
                            "$availableCategoryCount kategori",

                        modifier =
                            Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StorefrontInfoBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(14.dp),

        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant
    ) {
        Text(
            text =
                text,

            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .labelLarge
        )
    }
}

@Composable
private fun StorefrontMenuCategorySection(
    category:
    ProducerStorefrontMenuCategoryResponse,

    onFoodClick: (Int) -> Unit
) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Text(
            text =
                category.categoryName,

            style =
                MaterialTheme
                    .typography
                    .titleLarge,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "${category.foods.size} yemek",

            modifier =
                Modifier.padding(
                    top = 3.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        category.foods
            .forEach {
                    food ->

                StorefrontMenuFoodCard(
                    food = food,

                    onClick = {
                        onFoodClick(
                            food.id
                        )
                    }
                )
            }
    }
}

@Composable
private fun StorefrontMenuFoodCard(
    food:
    ProducerStorefrontMenuFoodResponse,

    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 7.dp
                )
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(18.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column {
            FoodImage(
                imageUrl =
                    food.imageUrl,

                contentDescription =
                    food.name,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            16f / 9f
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp
                            )
                        ),

                contentScale =
                    ContentScale.Crop
            )

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {
                Text(
                    text =
                        food.name,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    maxLines = 2,

                    overflow =
                        TextOverflow.Ellipsis
                )

                if (
                    food.description
                        .isNotBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    Text(
                        text =
                            food.description,

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,

                        maxLines = 2,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
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
                            formatPrice(
                                food.price
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Surface(
                        shape =
                            RoundedCornerShape(
                                50.dp
                            ),

                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    ) {
                        Text(
                            text =
                                "${food.preparationTimeMinutes} dk",

                            modifier =
                                Modifier.padding(
                                    horizontal =
                                        10.dp,

                                    vertical =
                                        6.dp
                                ),

                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge
                        )
                    }
                }
            }
        }
    }
}

private fun formatPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
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