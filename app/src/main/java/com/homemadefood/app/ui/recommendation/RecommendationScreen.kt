package com.homemadefood.app.ui.recommendation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerRecommendationResponse
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme
import java.util.Locale

@Composable
fun RecommendationScreen(
    uiState: RecommendationUiState,
    onBackClick: () -> Unit,
    onRetryAddressesClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onQuantityTextChange: (String) -> Unit,
    onAddressSelected: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onSelectRecommendationClick: (Int) -> Unit,
    onAddSelectedToCartClick: () -> Unit,
    onGoToCartClick: () -> Unit,
    onOpenFoodClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionEnabled =
        !uiState.isSearching &&
                uiState.selectingFoodId == null &&
                !uiState.isAddingToCart

    CustomerHomeTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomerHomeColors.Cream)
        ) {
            RecommendationHeader(
                onBackClick = onBackClick,
                backEnabled =
                    !uiState.isSearching &&
                            uiState.selectingFoodId == null
            )

            when {
                uiState.isLoadingAddresses -> {
                    RecommendationLoading(
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.addresses.isEmpty() -> {
                    RecommendationAddressRequired(
                        message =
                            uiState.errorMessage
                                ?: "Akıllı öneri alabilmek için kayıtlı bir teslimat adresiniz bulunmalıdır.",
                        onManageAddressesClick =
                            onManageAddressesClick,
                        onRetryAddressesClick =
                            onRetryAddressesClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 2.dp,
                            bottom = 30.dp
                        ),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        item(
                            key = "recommendation_intro"
                        ) {
                            RecommendationIntroCard()
                        }

                        item(
                            key = "recommendation_availability_info"
                        ) {
                            RecommendationAvailabilityInfoCard()
                        }

                        item(
                            key = "recommendation_search_form"
                        ) {
                            RecommendationSearchCard(
                                searchText =
                                    uiState.searchText,
                                quantityText =
                                    uiState.quantityText,
                                enabled =
                                    interactionEnabled,
                                onSearchTextChange =
                                    onSearchTextChange,
                                onQuantityTextChange =
                                    onQuantityTextChange
                            )
                        }

                        item(
                            key = "recommendation_address_title"
                        ) {
                            RecommendationAddressHeader(
                                onManageAddressesClick =
                                    onManageAddressesClick,
                                enabled =
                                    interactionEnabled
                            )
                        }

                        itemsIndexed(
                            items = uiState.addresses,
                            key = { _, address ->
                                address.id
                            }
                        ) { _, address ->
                            RecommendationAddressCard(
                                address = address,
                                isSelected =
                                    uiState.selectedAddressId ==
                                            address.id,
                                enabled =
                                    interactionEnabled,
                                onClick = {
                                    onAddressSelected(
                                        address.id
                                    )
                                }
                            )
                        }

                        item(
                            key = "recommendation_search_action"
                        ) {
                            Column {
                                Button(
                                    onClick =
                                        onSearchClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    enabled =
                                        !uiState.isSearching &&
                                                uiState.selectingFoodId == null &&
                                                uiState.searchText
                                                    .trim()
                                                    .length >= 2 &&
                                                uiState.selectedAddressId != null,
                                    shape =
                                        RoundedCornerShape(17.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor =
                                                CustomerHomeColors
                                                    .Terracotta,
                                            contentColor =
                                                Color.White,
                                            disabledContainerColor =
                                                CustomerHomeColors
                                                    .Terracotta
                                                    .copy(alpha = 0.35f),
                                            disabledContentColor =
                                                Color.White.copy(
                                                    alpha = 0.8f
                                                )
                                        )
                                ) {
                                    if (uiState.isSearching) {
                                        CircularProgressIndicator(
                                            modifier =
                                                Modifier.size(21.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                    } else {
                                        Text(
                                            text =
                                                "✦  En Uygun Üreticileri Bul",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .titleMedium,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }

                                if (
                                    !uiState.errorMessage
                                        .isNullOrBlank()
                                ) {
                                    Spacer(
                                        modifier =
                                            Modifier.height(12.dp)
                                    )

                                    RecommendationMessageCard(
                                        message =
                                            uiState.errorMessage,
                                        isError = true
                                    )
                                }

                                if (
                                    !uiState.actionMessage
                                        .isNullOrBlank()
                                ) {
                                    Spacer(
                                        modifier =
                                            Modifier.height(12.dp)
                                    )

                                    RecommendationMessageCard(
                                        message =
                                            uiState.actionMessage,
                                        isError = false
                                    )
                                }

                                if (
                                    !uiState.cartMessage
                                        .isNullOrBlank()
                                ) {
                                    Spacer(
                                        modifier =
                                            Modifier.height(12.dp)
                                    )

                                    RecommendationCartSuccessCard(
                                        message =
                                            uiState.cartMessage,
                                        onGoToCartClick =
                                            onGoToCartClick
                                    )
                                }
                            }
                        }

                        if (
                            uiState.recommendations
                                .isNotEmpty()
                        ) {
                            item(
                                key = "recommendation_result_header"
                            ) {
                                RecommendationResultsHeader(
                                    resultCount =
                                        uiState
                                            .recommendations
                                            .take(3)
                                            .size
                                )
                            }

                            itemsIndexed(
                                items =
                                    uiState.recommendations
                                        .take(3),
                                key = { _, recommendation ->
                                    recommendation.foodId
                                }
                            ) { index, recommendation ->
                                RecommendationCard(
                                    rank = index + 1,
                                    recommendation =
                                        recommendation,
                                    isSelecting =
                                        uiState.selectingFoodId ==
                                                recommendation.foodId,
                                    isSelected =
                                        uiState
                                            .selectedRecommendation
                                            ?.foodId ==
                                                recommendation.foodId,
                                    isAddingToCart =
                                        uiState.isAddingToCart &&
                                                uiState
                                                    .selectedRecommendation
                                                    ?.foodId ==
                                                recommendation.foodId,
                                    isAddedToCart =
                                        uiState.addedToCartFoodId ==
                                                recommendation.foodId,
                                    onSelectClick = {
                                        onSelectRecommendationClick(
                                            recommendation.foodId
                                        )
                                    },
                                    onAddSelectedToCartClick =
                                        onAddSelectedToCartClick,
                                    onOpenFoodClick = {
                                        onOpenFoodClick(
                                            recommendation.foodId
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationHeader(
    onBackClick: () -> Unit,
    backEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 14.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            enabled = backEnabled,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {
            Text(
                text = "←  Geri Dön",
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Akıllı Üretici Önerisi",
                    style =
                        MaterialTheme.typography
                            .headlineMedium,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "İhtiyacınıza en uygun üreticileri karşılaştırın.",
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors.TextMuted
                )
            }

            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color =
                    CustomerHomeColors.DeepOlive
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "✦",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        color =
                            CustomerHomeColors.Gold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationIntroCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors.DeepOlive,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 17.dp
            )
        ) {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "✦",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    color =
                        CustomerHomeColors.Gold
                )

                Text(
                    text = "Nasıl çalışıyor?",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Aradığınız yemek, miktar ve teslimat adresine göre sistem; yalnızca şu anda sipariş alan açık işletmeleri değerlendirir ve puan, mesafe, hazırlama süresi ile üretici kapasitesine göre en uygun ilk üç seçeneği sıralar.",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    Color.White.copy(alpha = 0.84f)
            )
        }
    }
}

@Composable
private fun RecommendationAvailabilityInfoCard() {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(16.dp),
        color =
            CustomerHomeColors.OliveSoft,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors
                        .DeepOlive
                        .copy(alpha = 0.16f)
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp),
            verticalAlignment =
                Alignment.Top
        ) {
            Text(
                text = "●",
                color =
                    CustomerHomeColors.DeepOlive,
                style =
                    MaterialTheme.typography
                        .labelLarge
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text =
                        "Yalnızca açık işletmeler önerilir",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        "Bir işletme aramadan sonra kapanırsa seçim veya sepete ekleme sistem tarafından engellenir. Bu durumda güncel öneriler için tekrar arama yapabilirsiniz.",
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        CustomerHomeColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun RecommendationSearchCard(
    searchText: String,
    quantityText: String,
    enabled: Boolean,
    onSearchTextChange: (String) -> Unit,
    onQuantityTextChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color =
                CustomerHomeColors.Outline
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Text(
                text = "Ne arıyorsunuz?",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = searchText,
                onValueChange =
                    onSearchTextChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Aradığınız yemek")
                },
                placeholder = {
                    Text(
                        "Örn. Mantı, çorba, sarma"
                    )
                },
                supportingText = {
                    Text(
                        "${searchText.length}/100"
                    )
                },
                singleLine = true,
                enabled = enabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = quantityText,
                onValueChange =
                    onQuantityTextChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Miktar")
                },
                placeholder = {
                    Text("1")
                },
                supportingText = {
                    Text("1 ile 100 arasında")
                },
                singleLine = true,
                enabled = enabled,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),
                shape =
                    RoundedCornerShape(15.dp)
            )
        }
    }
}

@Composable
private fun RecommendationAddressHeader(
    onManageAddressesClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 1.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Teslimat Adresi",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text =
                    "Öneri mesafe hesabında seçtiğiniz adresi kullanır.",
                style =
                    MaterialTheme.typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )
        }

        TextButton(
            onClick =
                onManageAddressesClick,
            enabled = enabled
        ) {
            Text(
                text = "Yönet",
                color =
                    CustomerHomeColors.Terracotta,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecommendationAddressCard(
    address: AddressResponse,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        color =
            if (isSelected) {
                CustomerHomeColors.OliveSoft
            } else {
                CustomerHomeColors.Surface
            },
        border = BorderStroke(
            width =
                if (isSelected) 1.5.dp
                else 1.dp,
            color =
                if (isSelected) {
                    CustomerHomeColors.DeepOlive
                        .copy(alpha = 0.55f)
                } else {
                    CustomerHomeColors.Outline
                }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                enabled = enabled,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor =
                            CustomerHomeColors
                                .DeepOlive,
                        unselectedColor =
                            CustomerHomeColors
                                .TextMuted
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = address.title,
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    if (address.isDefault) {
                        Spacer(
                            modifier =
                                Modifier.size(7.dp)
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(
                                    50.dp
                                ),
                            color =
                                CustomerHomeColors
                                    .SurfaceSoft
                        ) {
                            Text(
                                text = "Varsayılan",
                                modifier =
                                    Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 3.dp
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    CustomerHomeColors
                                        .TextMuted,
                                fontWeight =
                                    FontWeight
                                        .SemiBold
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = address.fullAddress,
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        CustomerHomeColors.TextMuted,
                    maxLines = 2,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    color =
                        CustomerHomeColors.DeepOlive,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecommendationResultsHeader(
    resultCount: Int
) {
    Column(
        modifier = Modifier.padding(
            top = 10.dp,
            bottom = 1.dp
        )
    ) {
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "✦",
                color =
                    CustomerHomeColors.Gold,
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Text(
                text = "Size Özel Öneriler",
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text =
                "$resultCount sonuç, en yüksek uygunluk puanından başlayarak sıralandı.",
            style =
                MaterialTheme.typography
                    .bodyMedium,
            color =
                CustomerHomeColors.TextMuted
        )
    }
}

@Composable
private fun RecommendationCard(
    rank: Int,
    recommendation:
    ProducerRecommendationResponse,
    isSelecting: Boolean,
    isSelected: Boolean,
    isAddingToCart: Boolean,
    isAddedToCart: Boolean,
    onSelectClick: () -> Unit,
    onAddSelectedToCartClick: () -> Unit,
    onOpenFoodClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color =
            CustomerHomeColors.Surface,
        shadowElevation =
            if (isSelected) 4.dp else 2.dp,
        border = BorderStroke(
            width =
                if (isSelected) 2.dp else 1.dp,
            color =
                if (isSelected) {
                    CustomerHomeColors.DeepOlive
                        .copy(alpha = 0.58f)
                } else {
                    CustomerHomeColors.Outline
                }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            RecommendationCardTop(
                rank = rank,
                recommendation =
                    recommendation,
                isSelected = isSelected
            )

            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 15.dp,
                    bottom = 15.dp
                )
            ) {
                RecommendationQuickFacts(
                    recommendation =
                        recommendation
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                HorizontalDivider(
                    color =
                        CustomerHomeColors.Outline
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Uygunluk Analizi",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                RecommendationScoreRow(
                    title = "Değerlendirme",
                    value =
                        formatRecommendationScore(
                            recommendation.ratingScore
                        )
                )

                RecommendationScoreRow(
                    title = "Mesafe",
                    value =
                        formatRecommendationScore(
                            recommendation.distanceScore
                        )
                )

                RecommendationScoreRow(
                    title = "Hazırlama",
                    value =
                        formatRecommendationScore(
                            recommendation.preparationScore
                        )
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(16.dp),
                    color =
                        CustomerHomeColors
                            .OliveSoft
                ) {
                    Column(
                        modifier =
                            Modifier.padding(13.dp)
                    ) {
                        Text(
                            text = "Neden önerildi?",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                recommendation
                                    .explanation,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                CustomerHomeColors.Text
                        )
                    }
                }

                if (isSelected) {
                    Spacer(
                        modifier =
                            Modifier.height(13.dp)
                    )

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            CustomerHomeColors
                                .TerracottaSoft
                    ) {
                        Text(
                            text =
                                "✓ Bu öneriyi seçtiniz",
                            modifier =
                                Modifier.padding(
                                    horizontal = 13.dp,
                                    vertical = 10.dp
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                CustomerHomeColors
                                    .Terracotta,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Button(
                    onClick = onSelectClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled =
                        !isSelecting &&
                                !isSelected &&
                                !isAddingToCart,
                    shape =
                        RoundedCornerShape(15.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                CustomerHomeColors
                                    .DeepOlive,
                            contentColor =
                                Color.White,
                            disabledContainerColor =
                                CustomerHomeColors
                                    .DeepOlive
                                    .copy(alpha = 0.35f),
                            disabledContentColor =
                                Color.White
                                    .copy(alpha = 0.8f)
                        )
                ) {
                    if (isSelecting) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text =
                                if (isSelected) {
                                    "Öneri Seçildi"
                                } else {
                                    "Bu Öneriyi Seç"
                                },
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }

                if (isSelected) {
                    Spacer(
                        modifier =
                            Modifier.height(9.dp)
                    )

                    Button(
                        onClick =
                            onAddSelectedToCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled =
                            !isAddingToCart &&
                                    !isAddedToCart,
                        shape =
                            RoundedCornerShape(15.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    CustomerHomeColors
                                        .Terracotta,
                                contentColor =
                                    Color.White,
                                disabledContainerColor =
                                    CustomerHomeColors
                                        .Terracotta
                                        .copy(
                                            alpha = 0.38f
                                        ),
                                disabledContentColor =
                                    Color.White.copy(
                                        alpha = 0.82f
                                    )
                            )
                    ) {
                        when {
                            isAddingToCart -> {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(
                                            20.dp
                                        ),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            }

                            isAddedToCart -> {
                                Text(
                                    text =
                                        "✓ Sepete Eklendi",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            else -> {
                                Text(
                                    text =
                                        "Seçilen Öneriyi Sepete Ekle",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                TextButton(
                    onClick =
                        onOpenFoodClick,
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !isSelecting &&
                                !isAddingToCart
                ) {
                    Text(
                        text =
                            "Yemeğin Detayını Gör",
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCardTop(
    rank: Int,
    recommendation:
    ProducerRecommendationResponse,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color =
            when {
                isSelected ->
                    CustomerHomeColors
                        .DeepOlive

                rank == 1 ->
                    CustomerHomeColors
                        .OliveSoft

                else ->
                    CustomerHomeColors
                        .SurfaceSoft
            }
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 15.dp
            )
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    shape =
                        RoundedCornerShape(50.dp),
                    color =
                        when {
                            isSelected ->
                                Color.White.copy(
                                    alpha = 0.14f
                                )

                            rank == 1 ->
                                CustomerHomeColors
                                    .DeepOlive

                            else ->
                                CustomerHomeColors
                                    .Outline
                        }
                ) {
                    Text(
                        text =
                            when (rank) {
                                1 -> "★  1. Öneri"
                                2 -> "2. Öneri"
                                else -> "3. Öneri"
                            },
                        modifier =
                            Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            ),
                        style =
                            MaterialTheme.typography
                                .labelLarge,
                        color =
                            when {
                                isSelected ->
                                    Color.White

                                rank == 1 ->
                                    Color.White

                                else ->
                                    CustomerHomeColors
                                        .DeepOlive
                            },
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {
                    Text(
                        text =
                            String.format(
                                Locale("tr", "TR"),
                                "%.2f",
                                recommendation.totalScore
                            ),
                        style =
                            MaterialTheme.typography
                                .headlineSmall,
                        color =
                            if (isSelected) {
                                CustomerHomeColors.Gold
                            } else {
                                CustomerHomeColors
                                    .Terracotta
                            },
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = "uygunluk puanı",
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            if (isSelected) {
                                Color.White.copy(
                                    alpha = 0.72f
                                )
                            } else {
                                CustomerHomeColors
                                    .TextMuted
                            }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = recommendation.foodName,
                style =
                    MaterialTheme.typography
                        .titleLarge,
                color =
                    if (isSelected) {
                        Color.White
                    } else {
                        CustomerHomeColors.Text
                    },
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text =
                    recommendation.businessName,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color =
                    if (isSelected) {
                        Color.White.copy(
                            alpha = 0.84f
                        )
                    } else {
                        CustomerHomeColors
                            .DeepOlive
                    },
                fontWeight =
                    FontWeight.SemiBold
            )

            if (
                !recommendation
                    .foodDescription
                    .isNullOrBlank()
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        recommendation
                            .foodDescription,
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        if (isSelected) {
                            Color.White.copy(
                                alpha = 0.76f
                            )
                        } else {
                            CustomerHomeColors
                                .TextMuted
                        },
                    maxLines = 3,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecommendationQuickFacts(
    recommendation:
    ProducerRecommendationResponse
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {
        RecommendationInformationRow(
            title = "Fiyat",
            value =
                formatRecommendationPrice(
                    recommendation.price
                ),
            emphasize = true
        )

        RecommendationInformationRow(
            title = "Mesafe",
            value =
                String.format(
                    Locale("tr", "TR"),
                    "%.2f km",
                    recommendation.distanceKm
                )
        )

        RecommendationInformationRow(
            title = "Hazırlama süresi",
            value =
                "${recommendation.preparationTimeMinutes} dk"
        )

        RecommendationInformationRow(
            title = "Üretici puanı",
            value =
                String.format(
                    Locale("tr", "TR"),
                    "%.2f / 5",
                    recommendation.averageRating
                )
        )

        RecommendationInformationRow(
            title = "Değerlendirme",
            value =
                "${recommendation.reviewCount} yorum"
        )

        RecommendationInformationRow(
            title = "Kalan kapasite",
            value =
                recommendation
                    .remainingCapacity
                    .toString()
        )
    }
}

@Composable
private fun RecommendationInformationRow(
    title: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .bodyMedium,
            color =
                CustomerHomeColors.TextMuted
        )

        Text(
            text = value,
            style =
                if (emphasize) {
                    MaterialTheme.typography
                        .titleMedium
                } else {
                    MaterialTheme.typography
                        .titleSmall
                },
            color =
                if (emphasize) {
                    CustomerHomeColors.Terracotta
                } else {
                    CustomerHomeColors.Text
                },
            fontWeight =
                if (emphasize) {
                    FontWeight.Bold
                } else {
                    FontWeight.SemiBold
                }
        )
    }
}

@Composable
private fun RecommendationScoreRow(
    title: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color =
            CustomerHomeColors.SurfaceSoft
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 9.dp
            ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography
                        .labelLarge,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(
        modifier = Modifier.height(6.dp)
    )
}

@Composable
private fun RecommendationMessageCard(
    message: String,
    isError: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color =
            if (isError) {
                CustomerHomeColors
                    .TerracottaSoft
            } else {
                CustomerHomeColors
                    .OliveSoft
            }
    ) {
        Text(
            text = message,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
            style =
                MaterialTheme.typography
                    .bodyMedium,
            color =
                if (isError) {
                    CustomerHomeColors.Error
                } else {
                    CustomerHomeColors.DeepOlive
                },
            fontWeight =
                FontWeight.Medium
        )
    }
}

@Composable
private fun RecommendationCartSuccessCard(
    message: String,
    onGoToCartClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors.OliveSoft,
        border = BorderStroke(
            width = 1.dp,
            color =
                CustomerHomeColors.DeepOlive
                    .copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "✓ $message",
                style =
                    MaterialTheme.typography
                        .titleSmall,
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onGoToCartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape =
                    RoundedCornerShape(14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            CustomerHomeColors
                                .DeepOlive,
                        contentColor =
                            Color.White
                    )
            ) {
                Text(
                    text = "Sepete Git",
                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RecommendationLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color =
                    CustomerHomeColors.Terracotta
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Teslimat adresleriniz hazırlanıyor...",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    CustomerHomeColors.TextMuted
            )
        }
    }
}

@Composable
private fun RecommendationAddressRequired(
    message: String,
    onManageAddressesClick: () -> Unit,
    onRetryAddressesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color =
                CustomerHomeColors.Surface,
            border = BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors.Outline
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color =
                        CustomerHomeColors
                            .TerracottaSoft
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "⌖",
                            style =
                                MaterialTheme.typography
                                    .headlineMedium,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Teslimat Adresi Gerekli",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    color =
                        CustomerHomeColors.DeepOlive,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Button(
                    onClick =
                        onManageAddressesClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape =
                        RoundedCornerShape(15.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                CustomerHomeColors
                                    .Terracotta,
                            contentColor =
                                Color.White
                        )
                ) {
                    Text(
                        text = "Adreslerime Git",
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick =
                        onRetryAddressesClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape =
                        RoundedCornerShape(15.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                                .copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = "Tekrar Dene",
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun formatRecommendationPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f ₺",
        price
    )
}

private fun formatRecommendationScore(
    score: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f",
        score
    )
}