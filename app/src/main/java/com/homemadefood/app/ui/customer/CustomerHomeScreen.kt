package com.homemadefood.app.ui.customer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.FoodResponse
import java.util.Locale
import androidx.compose.foundation.clickable

@Composable
fun CustomerHomeScreen(
    uiState: CustomerHomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
    onClearFiltersClick: () -> Unit,
    onRetryCategoriesClick: () -> Unit,
    onRetryFoodsClick: () -> Unit,
    onFoodClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hoş Geldiniz",
                    style =
                        MaterialTheme.typography
                            .headlineSmall
                )

                Text(
                    text =
                        "Bugün ne yemek istersiniz?",
                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }

            TextButton(
                onClick = onLogoutClick
            ) {
                Text("Çıkış")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = uiState.searchQuery,

            onValueChange =
                onSearchQueryChange,

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Yemek ara")
            },

            placeholder = {
                Text("Mantı, çorba, tatlı...")
            },

            singleLine = true,

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
                )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onSearchClick,
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                !uiState.isFoodsLoading
        ) {
            Text("Ara")
        }

        if (
            uiState.searchQuery.isNotBlank() ||
            uiState.selectedCategoryId != null
        ) {
            TextButton(
                onClick =
                    onClearFiltersClick,
                modifier =
                    Modifier.align(
                        Alignment.End
                    )
            ) {
                Text("Filtreleri Temizle")
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Kategoriler",
            style =
                MaterialTheme.typography
                    .titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        when {
            uiState.isCategoriesLoading -> {
                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )
            }

            uiState.categories.isEmpty() &&
                    uiState.errorMessage != null -> {

                Text(
                    text =
                        uiState.errorMessage,
                    color =
                        MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Button(
                    onClick =
                        onRetryCategoriesClick
                ) {
                    Text("Kategorileri Tekrar Yükle")
                }
            }

            uiState.categories.isEmpty() -> {
                Text(
                    text =
                        "Henüz aktif kategori bulunmuyor."
                )
            }

            else -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    FilterChip(
                        selected =
                            uiState.selectedCategoryId ==
                                    null,

                        onClick = {
                            onCategoryClick(null)
                        },

                        label = {
                            Text("Tümü")
                        }
                    )

                    uiState.categories.forEach {
                            category ->

                        FilterChip(
                            selected =
                                uiState
                                    .selectedCategoryId ==
                                        category.id,

                            onClick = {
                                onCategoryClick(
                                    category.id
                                )
                            },

                            label = {
                                Text(category.name)
                            }
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Yemekler",
            style =
                MaterialTheme.typography
                    .titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        when {
            uiState.isFoodsLoading -> {
                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )
            }

            uiState.foods.isEmpty() &&
                    uiState.errorMessage != null -> {

                Text(
                    text =
                        uiState.errorMessage,
                    color =
                        MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Button(
                    onClick =
                        onRetryFoodsClick,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Yemekleri Tekrar Yükle")
                }
            }

            uiState.foods.isEmpty() -> {
                Text(
                    text =
                        "Arama ölçütlerine uygun yemek bulunamadı.",
                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }

            else -> {
                uiState.foods.forEach { food ->
                    FoodCard(
                        food = food,
                        onClick = {
                            onFoodClick(food.id)
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Hızlı İşlemler",
            style =
                MaterialTheme.typography
                    .titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                // Öneri ekranı daha sonra bağlanacak.
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text("En Uygun Üreticiyi Bul")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                // Sipariş ekranı daha sonra bağlanacak.
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text("Siparişlerim")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onFavoritesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Favorilerim")
        }
    }
}

@Composable
private fun FoodCard(
    food: FoodResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 7.dp
            )
            .clickable(
                onClick = onClick
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {
            Text(
                text = food.name,
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = food.businessName,
                style =
                    MaterialTheme.typography
                        .labelLarge,
                color =
                    MaterialTheme.colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = food.description,
                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    text =
                        formatPrice(food.price),

                    style =
                        MaterialTheme.typography
                            .titleMedium
                )

                Text(
                    text =
                        "${food.preparationTimeMinutes} dk",

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Kategori: ${food.categoryName}",

                style =
                    MaterialTheme.typography
                        .bodySmall
            )
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