package com.homemadefood.app.ui.address

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.compose.material3.OutlinedButton
@Composable
fun EditAddressScreen(
    uiState: EditAddressUiState,

    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,

    onTitleChange:
        (String) -> Unit,

    onCityChange:
        (String) -> Unit,

    onDistrictChange:
        (String) -> Unit,

    onNeighborhoodChange:
        (String) -> Unit,

    onStreetChange:
        (String) -> Unit,

    onBuildingNoChange:
        (String) -> Unit,

    onFloorChange:
        (String) -> Unit,

    onApartmentNoChange:
        (String) -> Unit,

    onAddressNoteChange:
        (String) -> Unit,

    onIsDefaultChange:
        (Boolean) -> Unit,
    onSelectLocationClick:
        () -> Unit,

    onSaveClick:
        () -> Unit,

    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier =
                    modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null &&
                uiState.title.isBlank() -> {

            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text(
                        "← Adreslerime Dön"
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

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
                    onClick = onRetryClick,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }
        }

        else -> {
            val formEnabled =
                !uiState.isSaving &&
                        !uiState.isResolvingAddress

            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        )
            ) {
                TextButton(
                    onClick = onBackClick,

                    enabled =
                        !uiState.isSaving
                ) {
                    Text(
                        "← Adreslerime Dön"
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Teslimat Adresini Düzenle",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Text(
                    text =
                        "Adres bilgilerini kontrol edin ve gereken alanları güncelleyin.",

                    modifier =
                        Modifier.padding(
                            top = 6.dp
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )

                Text(
                    text =
                        "Teslimat Konumu",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                EditLocationCard(
                    uiState = uiState,
                    onSelectLocationClick =
                        onSelectLocationClick
                )

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                Text(
                    text =
                        "Adres Bilgileri",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Adres Başlığı *",

                    style =
                        MaterialTheme
                            .typography
                            .titleSmall
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    FilterChip(
                        selected =
                            uiState.title.equals(
                                "Ev",
                                ignoreCase =
                                    true
                            ),

                        onClick = {
                            onTitleChange(
                                "Ev"
                            )
                        },

                        label = {
                            Text("Ev")
                        },

                        enabled =
                            formEnabled
                    )

                    FilterChip(
                        selected =
                            uiState.title.equals(
                                "İş",
                                ignoreCase =
                                    true
                            ),

                        onClick = {
                            onTitleChange(
                                "İş"
                            )
                        },

                        label = {
                            Text("İş")
                        },

                        enabled =
                            formEnabled
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.title,

                    onValueChange =
                        onTitleChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Adres Başlığı"
                        )
                    },

                    singleLine = true,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.city,

                    onValueChange =
                        onCityChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("İl *")
                    },

                    singleLine = true,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.district,

                    onValueChange =
                        onDistrictChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("İlçe *")
                    },

                    singleLine = true,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.neighborhood,

                    onValueChange =
                        onNeighborhoodChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Mahalle *")
                    },

                    singleLine = true,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.street,

                    onValueChange =
                        onStreetChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Cadde / Sokak *"
                        )
                    },

                    singleLine = true,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    OutlinedTextField(
                        value =
                            uiState.buildingNo,

                        onValueChange =
                            onBuildingNoChange,

                        modifier =
                            Modifier.weight(1f),

                        label = {
                            Text("Bina No *")
                        },

                        singleLine = true,

                        enabled =
                            formEnabled
                    )

                    OutlinedTextField(
                        value =
                            uiState.floor,

                        onValueChange =
                            onFloorChange,

                        modifier =
                            Modifier.weight(1f),

                        label = {
                            Text("Kat")
                        },

                        singleLine = true,

                        enabled =
                            formEnabled,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            )
                    )

                    OutlinedTextField(
                        value =
                            uiState.apartmentNo,

                        onValueChange =
                            onApartmentNoChange,

                        modifier =
                            Modifier.weight(1f),

                        label = {
                            Text("Daire")
                        },

                        singleLine = true,

                        enabled =
                            formEnabled,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            )
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value =
                        uiState.addressNote,

                    onValueChange =
                        onAddressNoteChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Adres Tarifi"
                        )
                    },

                    placeholder = {
                        Text(
                            "Örn. Mavi kapılı bina, marketin karşısı..."
                        )
                    },

                    minLines = 2,
                    maxLines = 4,

                    enabled =
                        formEnabled
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                AddressPreviewCard(
                    fullAddress =
                        uiState.fullAddress
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked =
                            uiState.isDefault,

                        onCheckedChange =
                            onIsDefaultChange,

                        enabled =
                            formEnabled
                    )

                    Text(
                        text =
                            "Bu adresi varsayılan teslimat adresim yap"
                    )
                }

                if (
                    !uiState.errorMessage
                        .isNullOrBlank()
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            uiState.errorMessage,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                Button(
                    onClick =
                        onSaveClick,

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        uiState.canSave
                ) {
                    if (
                        uiState.isSaving
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
                            "Değişiklikleri Kaydet"
                        )
                    }
                }

                if (
                    !uiState.canSave &&
                    !uiState.isSaving &&
                    !uiState.isResolvingAddress
                ) {
                    Text(
                        text =
                            "Kaydetmek için * işaretli alanları tamamlayın.",

                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )
            }
        }
    }
}

@Composable
private fun EditLocationCard(
    uiState: EditAddressUiState,
    onSelectLocationClick: () -> Unit
){
    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            when {
                uiState.isResolvingAddress -> {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(
                                    22.dp
                                ),

                            strokeWidth =
                                2.dp
                        )

                        Text(
                            "Yeni konumun adres bilgileri bulunuyor..."
                        )
                    }
                }

                uiState.selectedLocation
                    ?.isValid() == true -> {

                    Text(
                        text =
                            "✓ Kayıtlı teslimat konumu mevcut",

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            "Konum koordinatları kullanıcıya gösterilmeden korunuyor.",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                else -> {
                    Text(
                        text =
                            "Bu adres için geçerli bir konum bulunamadı.",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }
            }

            if (
                !uiState
                    .locationLookupMessage
                    .isNullOrBlank()
            ) {
                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        uiState
                            .locationLookupMessage,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick =
                    onSelectLocationClick,

                modifier =
                    Modifier.fillMaxWidth(),

                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress
            ) {
                Text("Konumu Değiştir")
            }
        }
    }
}

@Composable
private fun AddressPreviewCard(
    fullAddress: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .secondaryContainer
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    "Kaydedilecek Adres",

                style =
                    MaterialTheme
                        .typography
                        .titleSmall,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    fullAddress.ifBlank {
                        "Adres bilgilerini tamamladıkça burada önizleme oluşacak."
                    },

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}