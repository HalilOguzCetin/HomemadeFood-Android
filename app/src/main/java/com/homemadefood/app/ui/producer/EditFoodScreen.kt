package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditFoodScreen(
    uiState: EditFoodUiState,

    onCategoryIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onPreparationTimeChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onAvailabilityChange: (Boolean) -> Unit,

    onSaveClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.foodId == null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Yemeklerime Dön")
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = uiState.errorMessage
                        ?: "Yemek bilgisi bulunamadı.",
                    color = MaterialTheme.colorScheme.error
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
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onBackClick,
                    enabled = !uiState.isSaving
                ) {
                    Text("← Yemeklerime Dön")
                }

                Text(
                    text = "Yemeği Düzenle",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Yemek numarası: ${uiState.foodId}",
                    style =
                        MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = uiState.categoryId,
                    onValueChange = onCategoryIdChange,
                    label = {
                        Text("Kategori ID")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = {
                        Text("Yemek Adı")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    label = {
                        Text("Yemek Açıklaması")
                    },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = onPriceChange,
                    label = {
                        Text("Fiyat")
                    },
                    supportingText = {
                        Text("Örnek: 195,00")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value =
                        uiState.preparationTimeMinutes,
                    onValueChange =
                        onPreparationTimeChange,
                    label = {
                        Text("Hazırlama Süresi")
                    },
                    suffix = {
                        Text("dk")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.imageUrl,
                    onValueChange = onImageUrlChange,
                    label = {
                        Text("Görsel URL Adresi")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Satış Durumu",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text =
                                if (uiState.isAvailable) {
                                    "Yemek şu anda satışta"
                                } else {
                                    "Yemek satışa kapalı"
                                },

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = uiState.isAvailable,
                        onCheckedChange =
                            onAvailabilityChange,
                        enabled = !uiState.isSaving
                    )
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }

                if (uiState.successMessage != null) {
                    Text(
                        text = uiState.successMessage,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Değişiklikleri Kaydet")
                    }
                }

                if (uiState.isSaving) {
                    Text(
                        text = "Yemek güncelleniyor...",
                        style =
                            MaterialTheme.typography.bodySmall,

                        modifier =
                            Modifier.align(
                                Alignment.CenterHorizontally
                            )
                    )
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}