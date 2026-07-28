package com.homemadefood.app.ui.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse
import com.homemadefood.app.data.model.ProducerRecommendationResponse
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick,
            enabled =
                !uiState.isSearching &&
                        uiState.selectingFoodId == null
        ) {
            Text("← Ana Sayfaya Dön")
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Akıllı Üretici Önerisi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "Aradığınız yemeği yazın. Sistem; puan, mesafe, " +
                        "hazırlama süresi ve kapasiteye göre en uygun " +
                        "üreticileri sıralasın.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        when {
            uiState.isLoadingAddresses -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.addresses.isEmpty() -> {
                Text(
                    text =
                        uiState.errorMessage
                            ?: "Öneri alabilmek için kayıtlı bir adresiniz bulunmalıdır.",
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Button(
                    onClick = onManageAddressesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adreslerime Git")
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Button(
                    onClick = onRetryAddressesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = uiState.searchText,

                            onValueChange =
                                onSearchTextChange,

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text("Aradığınız yemek")
                            },

                            placeholder = {
                                Text(
                                    "Örnek: Mantı, çorba, sarma"
                                )
                            },

                            supportingText = {
                                Text(
                                    "${uiState.searchText.length}/100"
                                )
                            },

                            singleLine = true,

                            enabled =
                                !uiState.isSearching &&
                                        uiState.selectingFoodId == null
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value = uiState.quantityText,

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

                            enabled =
                                !uiState.isSearching &&
                                        uiState.selectingFoodId == null
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Text(
                            text = "Teslimat Adresi",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
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
                                !uiState.isSearching &&
                                        uiState.selectingFoodId == null,

                            onClick = {
                                onAddressSelected(
                                    address.id
                                )
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Button(
                            onClick = onSearchClick,

                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                !uiState.isSearching &&
                                        uiState.selectingFoodId == null &&
                                        uiState.searchText
                                            .trim()
                                            .length >= 2 &&
                                        uiState.selectedAddressId != null
                        ) {
                            if (uiState.isSearching) {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.height(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("En Uygun Üreticileri Bul")
                            }
                        }

                        if (!uiState.errorMessage.isNullOrBlank()) {
                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            Text(
                                text = uiState.errorMessage,
                                color =
                                    MaterialTheme.colorScheme.error
                            )
                        }

                        if (!uiState.actionMessage.isNullOrBlank()) {
                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            Text(
                                text = uiState.actionMessage,
                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                        if (!uiState.cartMessage.isNullOrBlank()) {
                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            Text(
                                text = uiState.cartMessage,
                                color =
                                    MaterialTheme.colorScheme.primary,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Button(
                                onClick = onGoToCartClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sepete Git")
                            }
                        }

                        if (
                            uiState.recommendations.isNotEmpty()
                        ) {
                            Spacer(
                                modifier = Modifier.height(24.dp)
                            )

                            Text(
                                text = "Önerilen Üreticiler",
                                style =
                                    MaterialTheme.typography.headlineSmall
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Sonuçlar en yüksek uygunluk puanından " +
                                            "en düşüğe doğru sıralanmıştır.",
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )
                        }
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
                                        uiState.selectedRecommendation
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

                    item {
                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                enabled = enabled
            )

            Column(
                modifier =
                    Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = address.title,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    if (address.isDefault) {
                        Text(
                            text = "  • Varsayılan",
                            color =
                                MaterialTheme.colorScheme.primary,
                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = address.fullAddress,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }
        }
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
){
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "$rank. Öneri",
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        String.format(
                            Locale("tr", "TR"),
                            "%.2f puan",
                            recommendation.totalScore
                        ),

                    color =
                        MaterialTheme.colorScheme.primary,

                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = recommendation.foodName,
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Text(
                text = recommendation.businessName,
                color =
                    MaterialTheme.colorScheme.primary,
                style =
                    MaterialTheme.typography.titleMedium
            )

            if (
                !recommendation.foodDescription
                    .isNullOrBlank()
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        recommendation.foodDescription,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            RecommendationInformationRow(
                title = "Fiyat",
                value =
                    formatRecommendationPrice(
                        recommendation.price
                    )
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
                    "${recommendation.preparationTimeMinutes} dakika"
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
                    "${recommendation.remainingCapacity}"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Puan Dağılımı",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            RecommendationInformationRow(
                title = "Değerlendirme puanı",
                value =
                    formatRecommendationScore(
                        recommendation.ratingScore
                    )
            )

            RecommendationInformationRow(
                title = "Mesafe puanı",
                value =
                    formatRecommendationScore(
                        recommendation.distanceScore
                    )
            )

            RecommendationInformationRow(
                title = "Hazırlama puanı",
                value =
                    formatRecommendationScore(
                        recommendation.preparationScore
                    )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Neden önerildi?",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = recommendation.explanation,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            if (isSelected) {
                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "✓ Bu öneriyi seçtiniz",
                    color =
                        MaterialTheme.colorScheme.primary,
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onSelectClick,

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    !isSelecting &&
                            !isSelected
            ) {
                if (isSelecting) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (isSelected) {
                            "Öneri Seçildi"
                        } else {
                            "Bu Öneriyi Seç"
                        }
                    )
                }
            }
            if (isSelected) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Button(
                    onClick =
                        onAddSelectedToCartClick,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isAddingToCart &&
                                !isAddedToCart
                ) {
                    when {
                        isAddingToCart -> {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(22.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        isAddedToCart -> {
                            Text("Sepete Eklendi")
                        }

                        else -> {
                            Text(
                                "Seçilen Öneriyi Sepete Ekle"
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = onOpenFoodClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSelecting
            ) {
                Text("Yemeğin Detayını Gör")
            }
        }
    }
}

@Composable
private fun RecommendationInformationRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

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

private fun formatRecommendationPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
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