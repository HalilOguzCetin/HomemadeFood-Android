package com.homemadefood.app.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homemadefood.app.ui.components.FoodImage
import com.homemadefood.app.ui.customer.CustomerHomeCartButton
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.favoriteMessage) {
        val message = uiState.favoriteMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            onFavoriteMessageShown()
        }
    }

    LaunchedEffect(uiState.cartMessage) {
        val message = uiState.cartMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            onCartMessageShown()
        }
    }

    CustomerHomeTheme {
        Scaffold(
            modifier = modifier,
            containerColor = CustomerHomeColors.Cream,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                if (uiState.food != null && !uiState.isLoading && uiState.errorMessage == null) {
                    FoodDetailCartArea(
                        uiState = uiState,
                        isFoodAvailable = uiState.food.isAvailable,
                        onAddToCartClick = onAddToCartClick,
                        onIncreaseCartClick = onIncreaseCartClick,
                        onDecreaseCartClick = onDecreaseCartClick,
                        onGoToCartClick = onGoToCartClick
                    )
                }
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(CustomerHomeColors.Cream),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CustomerHomeColors.DeepOlive
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    FoodDetailError(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        onBackClick = onBackClick,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                uiState.food != null -> {
                    val food = uiState.food

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .background(CustomerHomeColors.Cream)
                    ) {
                        FoodDetailHero(
                            imageUrl = food.imageUrl,
                            foodName = food.name,
                            cartTotalQuantity = cartTotalQuantity,
                            onBackClick = onBackClick,
                            onGoToCartClick = onGoToCartClick
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 22.dp, bottom = 26.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = food.name,
                                    modifier = Modifier.weight(1f),
                                    color = CustomerHomeColors.DeepOlive,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 34.sp
                                    )
                                )

                                Text(
                                    text = formatPrice(food.price),
                                    color = CustomerHomeColors.Terracotta,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = food.description,
                                color = CustomerHomeColors.TextMuted,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            FoodInfoPanel(
                                preparationTimeMinutes = food.preparationTimeMinutes,
                                categoryName = food.categoryName,
                                isAvailable = food.isAvailable
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            BusinessPanel(
                                businessName = food.businessName
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            FavoriteButton(
                                uiState = uiState,
                                onFavoriteClick = onFavoriteClick
                            )
                        }
                    }
                }

                else -> {
                    FoodDetailError(
                        message = "Yemek bilgisi bulunamadı.",
                        onRetryClick = onRetryClick,
                        onBackClick = onBackClick,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodDetailHero(
    imageUrl: String,
    foodName: String,
    cartTotalQuantity: Int,
    onBackClick: () -> Unit,
    onGoToCartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(CustomerHomeColors.SurfaceSoft)
    ) {
        FoodImage(
            imageUrl = imageUrl,
            contentDescription = foodName,
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
                .size(48.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.94f),
            shadowElevation = 5.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(
                        text = "←",
                        color = CustomerHomeColors.DeepOlive,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        CustomerHomeCartButton(
            totalQuantity = cartTotalQuantity,
            onClick = onGoToCartClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp)
        )
    }
}

@Composable
private fun FoodInfoPanel(
    preparationTimeMinutes: Int,
    categoryName: String,
    isAvailable: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CustomerHomeColors.Surface,
        border = BorderStroke(1.dp, CustomerHomeColors.Outline),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            InfoItem(
                modifier = Modifier.weight(1f),
                symbol = "◷",
                title = "Hazırlama",
                value = "$preparationTimeMinutes dk"
            )

            InfoDivider()

            InfoItem(
                modifier = Modifier.weight(1f),
                symbol = "◇",
                title = "Kategori",
                value = categoryName
            )

            InfoDivider()

            InfoItem(
                modifier = Modifier.weight(1f),
                symbol = "✓",
                title = "Durum",
                value = if (isAvailable) "Satışta" else "Satışta değil"
            )
        }
    }
}

@Composable
private fun InfoItem(
    modifier: Modifier,
    symbol: String,
    title: String,
    value: String
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = CustomerHomeColors.OliveSoft
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = symbol,
                    color = CustomerHomeColors.Olive,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            color = CustomerHomeColors.TextMuted,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = CustomerHomeColors.Text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InfoDivider() {
    Box(
        modifier = Modifier
            .padding(top = 5.dp)
            .size(width = 1.dp, height = 70.dp)
            .background(CustomerHomeColors.Outline)
    )
}

@Composable
private fun BusinessPanel(
    businessName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CustomerHomeColors.Surface,
        border = BorderStroke(1.dp, CustomerHomeColors.Outline),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = CustomerHomeColors.OliveSoft
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⌂",
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.size(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hazırlayan işletme",
                    color = CustomerHomeColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = businessName,
                    color = CustomerHomeColors.DeepOlive,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun FavoriteButton(
    uiState: FoodDetailUiState,
    onFavoriteClick: () -> Unit
) {
    val loading = uiState.isFavoriteChecking || uiState.isFavoriteActionLoading

    OutlinedButton(
        onClick = onFavoriteClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !loading,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (uiState.isFavorite) {
                CustomerHomeColors.Terracotta
            } else {
                CustomerHomeColors.Outline
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (uiState.isFavorite) {
                CustomerHomeColors.TerracottaSoft
            } else {
                CustomerHomeColors.Surface
            },
            contentColor = if (uiState.isFavorite) {
                CustomerHomeColors.Terracotta
            } else {
                CustomerHomeColors.DeepOlive
            }
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = CustomerHomeColors.DeepOlive
            )
        } else {
            Text(
                text = if (uiState.isFavorite) {
                    "♥ Favorilerde"
                } else {
                    "♡ Favoriye Ekle"
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FoodDetailCartArea(
    uiState: FoodDetailUiState,
    isFoodAvailable: Boolean,
    onAddToCartClick: () -> Unit,
    onIncreaseCartClick: () -> Unit,
    onDecreaseCartClick: () -> Unit,
    onGoToCartClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CustomerHomeColors.Surface,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 14.dp
            )
        ) {
            when {
                uiState.isCartChecking -> {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = false,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                uiState.cartQuantity > 0 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            color = CustomerHomeColors.Cream,
                            border = BorderStroke(1.dp, CustomerHomeColors.Outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = onDecreaseCartClick,
                                    enabled = !uiState.isCartActionLoading
                                ) {
                                    Text(
                                        text = "−",
                                        color = CustomerHomeColors.DeepOlive,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (uiState.isCartActionLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp,
                                        color = CustomerHomeColors.DeepOlive
                                    )
                                } else {
                                    Text(
                                        text = uiState.cartQuantity.toString(),
                                        color = CustomerHomeColors.Text,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                TextButton(
                                    onClick = onIncreaseCartClick,
                                    enabled = !uiState.isCartActionLoading && uiState.cartQuantity < 50
                                ) {
                                    Text(
                                        text = "+",
                                        color = CustomerHomeColors.DeepOlive,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onGoToCartClick,
                            modifier = Modifier
                                .weight(1.35f)
                                .height(54.dp),
                            enabled = !uiState.isCartActionLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomerHomeColors.Terracotta,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Sepete Git",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> {
                    Button(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = isFoodAvailable && !uiState.isCartActionLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomerHomeColors.Terracotta,
                            contentColor = Color.White,
                            disabledContainerColor = CustomerHomeColors.Outline,
                            disabledContentColor = CustomerHomeColors.TextMuted
                        )
                    ) {
                        if (uiState.isCartActionLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = if (isFoodAvailable) {
                                    "Sepete Ekle"
                                } else {
                                    "Satışta Değil"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodDetailError(
    message: String,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = CustomerHomeColors.Error,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomerHomeColors.DeepOlive,
                contentColor = Color.White
            )
        ) {
            Text("Tekrar Dene")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackClick) {
            Text(
                text = "← Geri",
                color = CustomerHomeColors.DeepOlive
            )
        }
    }
}

private fun formatPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f ₺",
        price
    )
}