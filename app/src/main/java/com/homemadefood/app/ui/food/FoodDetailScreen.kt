package com.homemadefood.app.ui.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun FoodDetailScreen(
    uiState: FoodDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Geri")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color =
                        MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }

            uiState.food != null -> {
                val food = uiState.food

                Text(
                    text = food.name,
                    style =
                        MaterialTheme.typography
                            .headlineMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = food.businessName,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = food.description,
                    style =
                        MaterialTheme.typography
                            .bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                DetailRow(
                    title = "Kategori",
                    value = food.categoryName
                )

                DetailRow(
                    title = "Hazırlama süresi",
                    value =
                        "${food.preparationTimeMinutes} dakika"
                )

                DetailRow(
                    title = "Fiyat",
                    value = formatPrice(food.price)
                )

                DetailRow(
                    title = "Durum",
                    value =
                        if (food.isAvailable) {
                            "Satışta"
                        } else {
                            "Satışta değil"
                        }
                )

                if (!uiState.favoriteMessage.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = uiState.favoriteMessage,
                        color =
                            if (uiState.isFavoriteError) {
                                MaterialTheme
                                    .colorScheme.error
                            } else {
                                MaterialTheme
                                    .colorScheme.primary
                            },
                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = onFavoriteClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        !uiState.isFavoriteChecking &&
                                !uiState.isFavoriteActionLoading
                ) {
                    when {
                        uiState.isFavoriteChecking -> {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(22.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        uiState.isFavoriteActionLoading -> {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(22.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        uiState.isFavorite -> {
                            Text("Favoriden Çıkar")
                        }

                        else -> {
                            Text("Favoriye Ekle")
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                if (!uiState.cartMessage.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = uiState.cartMessage,
                        color =
                            if (uiState.isCartError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onAddToCartClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        food.isAvailable &&
                                !uiState.isCartActionLoading
                ) {
                    if (uiState.isCartActionLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sepete Ekle")
                    }
                }
            }

            else -> {
                Text(
                    text = "Yemek bilgisi bulunamadı."
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleSmall
        )
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