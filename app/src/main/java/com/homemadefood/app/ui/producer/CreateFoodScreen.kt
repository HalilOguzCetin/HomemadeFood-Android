package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreateFoodScreen(
    uiState: CreateFoodUiState,

    onCategoryIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onPreparationTimeChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,

    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,

    modifier: Modifier = Modifier
) {
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
            text = "Yeni Yemek Ekle",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text =
                "Yemeğin satış bilgilerini eksiksiz doldurun.",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        OutlinedTextField(
            value = uiState.categoryId,
            onValueChange = onCategoryIdChange,
            label = {
                Text("Kategori ID")
            },
            supportingText = {
                Text(
                    "Veritabanındaki geçerli kategori numarasını girin."
                )
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
            supportingText = {
                Text(
                    "Şimdilik internetteki görsel bağlantısını girin."
                )
            },
            singleLine = true,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color =
                    MaterialTheme.colorScheme.error,
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.successMessage != null) {
            Text(
                text = uiState.successMessage,
                color =
                    MaterialTheme.colorScheme.primary,
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

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

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            } else {
                Text("Yemeği Kaydet")
            }
        }

        if (uiState.isSaving) {
            Text(
                text = "Yemek kaydediliyor...",
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