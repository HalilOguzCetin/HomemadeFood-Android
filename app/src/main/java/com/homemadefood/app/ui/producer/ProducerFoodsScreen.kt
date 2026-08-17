package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.FoodResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import java.util.Locale

@Composable
fun ProducerFoodsScreen(
    uiState: ProducerFoodsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAddFoodClick: () -> Unit,
    onEditFoodClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Üretici Paneline Dön")
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Yemeklerim",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(
            onClick = onAddFoodClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Yeni Yemek Ekle")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when {
            uiState.isLoading -> {
                AppLoadingState(
                    modifier = Modifier.fillMaxSize(),
                    message = "Yemekleriniz yükleniyor..."
                )
            }

            uiState.errorMessage != null -> {
                AppErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.foods.isEmpty() -> {
                AppEmptyState(
                    title = "Henüz yemek eklemediniz",
                    message =
                        "Yeni Yemek Ekle butonunu kullanarak menünüze ilk yemeğinizi ekleyebilirsiniz.",
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.foods,
                        key = { food ->
                            food.id
                        }
                    ) { food ->
                        ProducerFoodCard(
                            food = food,

                            onEditClick = {
                                onEditFoodClick(
                                    food.id
                                )
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProducerFoodCard(
    food: FoodResponse,
    onEditClick: () -> Unit
) {
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
                    text = food.name,
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Text(
                    text =
                        if (food.isAvailable) {
                            "Satışta"
                        } else {
                            "Satışta Değil"
                        },

                    color =
                        if (food.isAvailable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },

                    style =
                        MaterialTheme.typography.titleSmall
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = food.categoryName,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            if (food.description.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProducerFoodInformationRow(
                title = "Fiyat",
                value = formatProducerFoodPrice(food.price)
            )

            ProducerFoodInformationRow(
                title = "Hazırlama süresi",
                value =
                    "${food.preparationTimeMinutes} dakika"
            )

            ProducerFoodInformationRow(
                title = "Kategori",
                value = food.categoryName
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Yemeği Düzenle")
            }
        }
    }
}

@Composable
private fun ProducerFoodInformationRow(
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
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

private fun formatProducerFoodPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}