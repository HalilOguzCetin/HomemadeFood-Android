package com.homemadefood.app.ui.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.homemadefood.app.ui.components.CustomerCartButton
import com.homemadefood.app.ui.components.FoodImage
import java.util.Locale

@Composable
fun FoodDetailScreen(
    uiState: FoodDetailUiState,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onIncreaseCartClick: () -> Unit,
    onDecreaseCartClick: () -> Unit,
    onGoToCartClick: () -> Unit,
    onFavoriteMessageShown: () -> Unit = {},
    onCartMessageShown: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(
        uiState.favoriteMessage
    ) {
        val message =
            uiState.favoriteMessage

        if (!message.isNullOrBlank()) {
            snackbarHostState
                .showSnackbar(message)

            onFavoriteMessageShown()
        }
    }

    LaunchedEffect(
        uiState.cartMessage
    ) {
        val message =
            uiState.cartMessage

        if (!message.isNullOrBlank()) {
            snackbarHostState
                .showSnackbar(message)

            onCartMessageShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState =
                    snackbarHostState
            )
        }
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(innerPadding)
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
                        onGoToCartClick
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
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
                        onClick =
                            onRetryClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Tekrar Dene")
                    }
                }

                uiState.food != null -> {
                    val food =
                        uiState.food

                    FoodImage(
                        imageUrl =
                            food.imageUrl,

                        contentDescription =
                            food.name,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(
                                    RoundedCornerShape(
                                        20.dp
                                    )
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    Text(
                        text = food.name,

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            food.businessName,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    Text(
                        text =
                            food.description,

                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    DetailRow(
                        title = "Kategori",
                        value =
                            food.categoryName
                    )

                    DetailRow(
                        title =
                            "Hazırlama süresi",

                        value =
                            "${food.preparationTimeMinutes} dakika"
                    )

                    DetailRow(
                        title = "Fiyat",
                        value =
                            formatPrice(
                                food.price
                            )
                    )

                    DetailRow(
                        title = "Durum",

                        value =
                            if (
                                food.isAvailable
                            ) {
                                "Satışta"
                            } else {
                                "Satışta değil"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Button(
                        onClick =
                            onFavoriteClick,

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            !uiState
                                .isFavoriteChecking &&
                                    !uiState
                                        .isFavoriteActionLoading
                    ) {
                        if (
                            uiState
                                .isFavoriteChecking ||
                            uiState
                                .isFavoriteActionLoading
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(
                                        22.dp
                                    ),

                                strokeWidth =
                                    2.dp
                            )
                        } else if (
                            uiState.isFavorite
                        ) {
                            Text(
                                "♥ Favorilerde"
                            )
                        } else {
                            Text(
                                "♡ Favoriye Ekle"
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    when {
                        uiState.isCartChecking -> {
                            Button(
                                onClick = {},
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                enabled = false
                            ) {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.height(
                                            22.dp
                                        ),

                                    strokeWidth =
                                        2.dp
                                )
                            }
                        }

                        uiState.cartQuantity > 0 -> {
                            Text(
                                text = "Sepette",

                                modifier =
                                    Modifier.align(
                                        Alignment
                                            .CenterHorizontally
                                    ),

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        10.dp
                                    )
                            )

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement
                                        .SpaceEvenly,

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick =
                                        onDecreaseCartClick,

                                    enabled =
                                        !uiState
                                            .isCartActionLoading
                                ) {
                                    Text("−")
                                }

                                if (
                                    uiState
                                        .isCartActionLoading
                                ) {
                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier.height(
                                                24.dp
                                            ),

                                        strokeWidth =
                                            2.dp
                                    )
                                } else {
                                    Text(
                                        text =
                                            uiState
                                                .cartQuantity
                                                .toString(),

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleLarge
                                    )
                                }

                                OutlinedButton(
                                    onClick =
                                        onIncreaseCartClick,

                                    enabled =
                                        !uiState
                                            .isCartActionLoading &&
                                                uiState
                                                    .cartQuantity < 50
                                ) {
                                    Text("+")
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        14.dp
                                    )
                            )

                            Button(
                                onClick =
                                    onGoToCartClick,

                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    !uiState
                                        .isCartActionLoading
                            ) {
                                Text(
                                    "Sepete Git"
                                )
                            }
                        }

                        else -> {
                            Button(
                                onClick =
                                    onAddToCartClick,

                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    food.isAvailable &&
                                            !uiState
                                                .isCartActionLoading
                            ) {
                                if (
                                    uiState
                                        .isCartActionLoading
                                ) {
                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier.height(
                                                22.dp
                                            ),

                                        strokeWidth =
                                            2.dp
                                    )
                                } else {
                                    Text(
                                        if (
                                            food.isAvailable
                                        ) {
                                            "Sepete Ekle"
                                        } else {
                                            "Satışta Değil"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text =
                            "Yemek bilgisi bulunamadı."
                    )
                }
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
        modifier =
            Modifier
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
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Text(
            text = value,

            style =
                MaterialTheme
                    .typography
                    .titleSmall
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