package com.homemadefood.app.ui.producer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CreateFoodScreen(
    uiState: CreateFoodUiState,

    onCategorySelected: (Int) -> Unit,
    onRetryCategoriesClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onPreparationTimeChange: (String) -> Unit,

    onImageSelected: (String) -> Unit,
    onRemoveImage: () -> Unit,

    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                onImageSelected(uri.toString())
            }
        }

    fun openPhotoPicker() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts
                    .PickVisualMedia
                    .ImageOnly
            )
        )
    }

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

        Text(
            text = "Yemek Fotoğrafı *",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text =
                "Galerinizden yemeği net gösteren tek bir fotoğraf seçin.",
            style = MaterialTheme.typography.bodySmall
        )

        if (uiState.selectedImageUri.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Henüz fotoğraf seçilmedi",
                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        OutlinedButton(
                            onClick = ::openPhotoPicker,
                            enabled = !uiState.isSaving
                        ) {
                            Text("Fotoğraf Seç")
                        }
                    }
                }
            }
        } else {
            AsyncImage(
                model = uiState.selectedImageUri,
                contentDescription =
                    "Seçilen yemek fotoğrafı",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = ::openPhotoPicker,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fotoğrafı Değiştir")
                }

                TextButton(
                    onClick = onRemoveImage,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fotoğrafı Kaldır")
                }
            }
        }

        FoodCategorySelector(
            categories = uiState.categories,
            selectedCategoryId =
                uiState.selectedCategoryId,
            selectedCategoryName =
                uiState.selectedCategoryName,
            isLoading =
                uiState.isCategoriesLoading,
            errorMessage =
                uiState.categoryErrorMessage,
            onCategorySelected =
                onCategorySelected,
            onRetryClick =
                onRetryCategoriesClick,
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
            enabled = uiState.canSave,
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

        if (uiState.selectedImageUri.isNullOrBlank()) {
            Text(
                text =
                    "Yemeği kaydetmek için fotoğraf seçmek zorunludur.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
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