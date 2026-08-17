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
import androidx.compose.material3.Switch
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
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import com.homemadefood.app.ui.components.FoodImage

@Composable
fun EditFoodScreen(
    uiState: EditFoodUiState,

    onCategorySelected: (Int) -> Unit,
    onRetryCategoriesClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onPreparationTimeChange: (String) -> Unit,

    onImageSelected: (String) -> Unit,
    onCancelImageSelection: () -> Unit,

    onAvailabilityChange: (Boolean) -> Unit,

    onSaveClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                onImageSelected(
                    uri.toString()
                )
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

    when {
        uiState.isLoading -> {
            AppLoadingState(
                modifier = modifier.fillMaxSize(),
                message = "Yemek bilgileri yükleniyor..."
            )
        }

        uiState.foodId == null -> {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                AppErrorState(
                    message =
                        uiState.errorMessage
                            ?: "Yemek bilgisi bulunamadı.",
                    onRetryClick = onRetryClick,
                    modifier = Modifier.fillMaxSize()
                )

                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text("← Yemeklerime Dön")
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

                Text(
                    text = "Yemek Fotoğrafı *",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text =
                        if (
                            uiState.selectedImageUri
                                .isNullOrBlank()
                        ) {
                            "Mevcut fotoğraf korunacak. Değiştirmek isterseniz galerinizden yeni bir fotoğraf seçin."
                        } else {
                            "Yeni seçilen fotoğraf kaydettiğinizde mevcut fotoğrafın yerini alacak."
                        },
                    style =
                        MaterialTheme.typography.bodySmall
                )

                if (
                    !uiState.selectedImageUri
                        .isNullOrBlank()
                ) {
                    AsyncImage(
                        model =
                            uiState.selectedImageUri,
                        contentDescription =
                            "Yeni seçilen yemek fotoğrafı",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(
                                RoundedCornerShape(
                                    16.dp
                                )
                            ),
                        contentScale =
                            ContentScale.Crop
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {
                        OutlinedButton(
                            onClick =
                                ::openPhotoPicker,
                            enabled =
                                !uiState.isSaving,
                            modifier =
                                Modifier.weight(1f)
                        ) {
                            Text(
                                "Başka Fotoğraf Seç"
                            )
                        }

                        TextButton(
                            onClick =
                                onCancelImageSelection,
                            enabled =
                                !uiState.isSaving,
                            modifier =
                                Modifier.weight(1f)
                        ) {
                            Text(
                                "Seçimi İptal Et"
                            )
                        }
                    }
                } else {
                    FoodImage(
                        imageUrl =
                            uiState.imageUrl,
                        contentDescription =
                            "Mevcut yemek fotoğrafı",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(
                                RoundedCornerShape(
                                    16.dp
                                )
                            ),
                        contentScale =
                            ContentScale.Crop
                    )

                    OutlinedButton(
                        onClick =
                            ::openPhotoPicker,
                        enabled =
                            !uiState.isSaving,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Fotoğrafı Değiştir"
                        )
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
                    enabled =
                        !uiState.isSaving &&
                                !uiState.isLoading,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = {
                        Text("Yemek Adı")
                    },
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange =
                        onDescriptionChange,
                    label = {
                        Text("Yemek Açıklaması")
                    },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !uiState.isSaving,
                    modifier =
                        Modifier.fillMaxWidth()
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
                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value =
                        uiState
                            .preparationTimeMinutes,
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
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Satış Durumu",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Text(
                            text =
                                if (
                                    uiState
                                        .isAvailable
                                ) {
                                    "Yemek şu anda satışta"
                                } else {
                                    "Yemek satışa kapalı"
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }

                    Switch(
                        checked =
                            uiState.isAvailable,
                        onCheckedChange =
                            onAvailabilityChange,
                        enabled =
                            !uiState.isSaving
                    )
                }

                if (
                    !uiState.errorMessage
                        .isNullOrBlank()
                ) {
                    AppInlineMessage(
                        message =
                            uiState.errorMessage,
                        type =
                            AppMessageType.Error
                    )
                }

                if (
                    !uiState.successMessage
                        .isNullOrBlank()
                ) {
                    AppInlineMessage(
                        message =
                            uiState.successMessage,
                        type =
                            AppMessageType.Success
                    )
                }

                Button(
                    onClick = onSaveClick,
                    enabled = uiState.canSave,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(
                                    22.dp
                                ),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Değişiklikleri Kaydet"
                        )
                    }
                }

                if (uiState.isSaving) {
                    Text(
                        text =
                            "Yemek güncelleniyor...",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        modifier =
                            Modifier.align(
                                Alignment
                                    .CenterHorizontally
                            )
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )
            }
        }
    }
}