package com.homemadefood.app.ui.address

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homemadefood.app.ui.customer.CustomerHomeColors
import com.homemadefood.app.ui.customer.CustomerHomeTheme

@Composable
fun EditAddressScreen(
    uiState: EditAddressUiState,

    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,

    onTitleChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onBuildingNoChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onApartmentNoChange: (String) -> Unit,
    onAddressNoteChange: (String) -> Unit,

    onIsDefaultChange: (Boolean) -> Unit,

    onSelectLocationClick: () -> Unit,

    onSaveClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        when {
            uiState.isLoading -> {
                EditAddressLoadingScreen(
                    onBackClick = onBackClick,
                    modifier = modifier
                )
            }

            uiState.errorMessage != null &&
                    uiState.title.isBlank() -> {
                EditAddressLoadErrorScreen(
                    message = uiState.errorMessage,
                    onBackClick = onBackClick,
                    onRetryClick = onRetryClick,
                    modifier = modifier
                )
            }

            else -> {
                EditAddressContent(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    onTitleChange = onTitleChange,
                    onCityChange = onCityChange,
                    onDistrictChange = onDistrictChange,
                    onNeighborhoodChange =
                        onNeighborhoodChange,
                    onStreetChange = onStreetChange,
                    onBuildingNoChange =
                        onBuildingNoChange,
                    onFloorChange = onFloorChange,
                    onApartmentNoChange =
                        onApartmentNoChange,
                    onAddressNoteChange =
                        onAddressNoteChange,
                    onIsDefaultChange =
                        onIsDefaultChange,
                    onSelectLocationClick =
                        onSelectLocationClick,
                    onSaveClick = onSaveClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun EditAddressLoadingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {
            Text(
                text = "←  Adreslerime Dön",
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
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
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Adres bilgileriniz yükleniyor...",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        CustomerHomeColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun EditAddressLoadErrorScreen(
    message: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {
            Text(
                text = "←  Adreslerime Dön",
                color = CustomerHomeColors.DeepOlive,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CustomerHomeColors.Surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = CustomerHomeColors.Outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Adres Yüklenemedi",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color =
                            CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = message,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            CustomerHomeColors.TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
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
                            text = "Tekrar Dene",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditAddressContent(
    uiState: EditAddressUiState,
    onBackClick: () -> Unit,

    onTitleChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onBuildingNoChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onApartmentNoChange: (String) -> Unit,
    onAddressNoteChange: (String) -> Unit,

    onIsDefaultChange: (Boolean) -> Unit,

    onSelectLocationClick: () -> Unit,

    onSaveClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    val formEnabled =
        !uiState.isSaving &&
                !uiState.isResolvingAddress

    val requiresAddressCompletion =
        uiState.selectedLocation
            ?.isValid() == true &&
                (
                        uiState.city.isBlank() ||
                                uiState.district.isBlank() ||
                                uiState.neighborhood.isBlank() ||
                                uiState.street.isBlank() ||
                                uiState.buildingNo.isBlank()
                        )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomerHomeColors.Cream)
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 28.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            enabled = !uiState.isSaving,
            contentPadding =
                PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "←  Adreslerime Dön",
                color =
                    CustomerHomeColors.DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Adresi Düzenle",
            style =
                MaterialTheme
                    .typography
                    .headlineMedium,
            fontWeight = FontWeight.Bold,
            color =
                CustomerHomeColors.DeepOlive
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                "Kayıtlı teslimat adresinizi ve harita konumunu buradan güncelleyebilirsiniz.",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                CustomerHomeColors.TextMuted
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        EditAddressSectionHeader(
            step = "1",
            title = "Teslimat Konumu",
            subtitle =
                "Mevcut konumu koruyabilir veya haritadan yeni bir nokta seçebilirsiniz."
        )

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        EditAddressLocationCard(
            uiState = uiState,
            onSelectLocationClick =
                onSelectLocationClick
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        EditAddressSectionHeader(
            step = "2",
            title = "Adres Bilgileri",
            subtitle =
                if (uiState.isResolvingAddress) {
                    "Yeni konumdan adres bilgileri hazırlanıyor."
                } else {
                    "Kayıtlı bilgileri kontrol edip değiştirmek istediğiniz alanları güncelleyin."
                }
        )

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        if (requiresAddressCompletion) {
            EditLegacyAddressWarning()

            Spacer(
                modifier = Modifier.height(11.dp)
            )
        }

        EditAddressFormCard(
            uiState = uiState,
            formEnabled = formEnabled,
            onTitleChange = onTitleChange,
            onCityChange = onCityChange,
            onDistrictChange = onDistrictChange,
            onNeighborhoodChange =
                onNeighborhoodChange,
            onStreetChange = onStreetChange,
            onBuildingNoChange =
                onBuildingNoChange,
            onFloorChange = onFloorChange,
            onApartmentNoChange =
                onApartmentNoChange,
            onAddressNoteChange =
                onAddressNoteChange
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        EditAddressPreviewCard(
            fullAddress = uiState.fullAddress
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        EditDefaultAddressCard(
            checked = uiState.isDefault,
            enabled = !uiState.isSaving,
            onCheckedChange =
                onIsDefaultChange
        )

        if (
            !uiState.errorMessage
                .isNullOrBlank()
        ) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            EditAddressErrorCard(
                message = uiState.errorMessage
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.canSave,
            shape = RoundedCornerShape(17.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        CustomerHomeColors
                            .Terracotta,
                    contentColor = Color.White,
                    disabledContainerColor =
                        CustomerHomeColors
                            .Terracotta
                            .copy(alpha = 0.36f),
                    disabledContentColor =
                        Color.White
                            .copy(alpha = 0.82f)
                )
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(21.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text =
                        "Değişiklikleri Kaydet",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (
            !uiState.canSave &&
            !uiState.isSaving &&
            !uiState.isResolvingAddress
        ) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Kaydetmek için geçerli bir konum ve * işaretli adres alanları gereklidir.",
                modifier =
                    Modifier.fillMaxWidth(),
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    CustomerHomeColors.TextMuted
            )
        }
    }
}

@Composable
private fun EditAddressSectionHeader(
    step: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(11.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color =
                CustomerHomeColors.DeepOlive
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = step,
                    color = Color.White,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    CustomerHomeColors
                        .DeepOlive
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    CustomerHomeColors
                        .TextMuted
            )
        }
    }
}

@Composable
private fun EditAddressLocationCard(
    uiState: EditAddressUiState,
    onSelectLocationClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors
                        .Outline
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {
            when {
                uiState.isResolvingAddress -> {
                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            CustomerHomeColors
                                .SurfaceSoft
                    ) {
                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal =
                                        13.dp,
                                    vertical =
                                        12.dp
                                ),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color =
                                    CustomerHomeColors
                                        .Terracotta
                            )

                            Text(
                                text =
                                    "Yeni konum seçildi, adres bilgileri bulunuyor...",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    CustomerHomeColors.Text
                            )
                        }
                    }
                }

                uiState.selectedLocation
                    ?.isValid() == true -> {
                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            CustomerHomeColors
                                .OliveSoft
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(13.dp)
                        ) {
                            Text(
                                text =
                                    "✓ Kayıtlı teslimat konumu mevcut",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    CustomerHomeColors
                                        .DeepOlive
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Koordinatlar güvenli şekilde arka planda tutulur ve ekranda gösterilmez.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )

                            if (
                                !uiState
                                    .locationLookupMessage
                                    .isNullOrBlank()
                            ) {
                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
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
                                        CustomerHomeColors
                                            .Terracotta,
                                    fontWeight =
                                        FontWeight
                                            .Medium
                                )
                            }
                        }
                    }
                }

                else -> {
                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            CustomerHomeColors
                                .TerracottaSoft
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(13.dp)
                        ) {
                            Text(
                                text =
                                    "Teslimat konumu eksik",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    CustomerHomeColors
                                        .Error
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Bu adresi kaydedebilmek için haritadan geçerli bir teslimat noktası seçin.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick =
                    onSelectLocationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled =
                    !uiState.isSaving &&
                            !uiState.isResolvingAddress,
                shape =
                    RoundedCornerShape(15.dp),
                border =
                    BorderStroke(
                        width = 1.dp,
                        color =
                            CustomerHomeColors
                                .DeepOlive
                                .copy(alpha = 0.35f)
                    )
            ) {
                Text(
                    text =
                        if (
                            uiState.selectedLocation
                                ?.isValid() == true
                        ) {
                            "Haritada Konumu Değiştir"
                        } else {
                            "Haritada Konum Seç"
                        },
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

@Composable
private fun EditLegacyAddressWarning() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color =
            CustomerHomeColors
                .TerracottaSoft
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text =
                    "Adres bilgilerini tamamlayın",
                style =
                    MaterialTheme
                        .typography
                        .titleSmall,
                fontWeight = FontWeight.Bold,
                color =
                    CustomerHomeColors
                        .Terracotta
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Bu kayıt eski adres yapısından kalmış olabilir. Boş olan zorunlu alanları kontrol edip tamamlayın.",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    CustomerHomeColors
                        .TextMuted
            )
        }
    }
}

@Composable
private fun EditAddressFormCard(
    uiState: EditAddressUiState,
    formEnabled: Boolean,

    onTitleChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onBuildingNoChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onApartmentNoChange: (String) -> Unit,
    onAddressNoteChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors.Surface,
        shadowElevation = 2.dp,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors
                        .Outline
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {
            Text(
                text = "Adres Başlığı *",
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                color =
                    CustomerHomeColors
                        .TextMuted,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                EditAddressTitleChip(
                    text = "Ev",
                    selected =
                        uiState.title.equals(
                            "Ev",
                            ignoreCase = true
                        ),
                    enabled = formEnabled,
                    onClick = {
                        onTitleChange("Ev")
                    }
                )

                EditAddressTitleChip(
                    text = "İş",
                    selected =
                        uiState.title.equals(
                            "İş",
                            ignoreCase = true
                        ),
                    enabled = formEnabled,
                    onClick = {
                        onTitleChange("İş")
                    }
                )

                EditAddressTitleChip(
                    text = "Diğer",
                    selected =
                        uiState.title.isNotBlank() &&
                                !uiState.title.equals(
                                    "Ev",
                                    ignoreCase = true
                                ) &&
                                !uiState.title.equals(
                                    "İş",
                                    ignoreCase = true
                                ),
                    enabled = formEnabled,
                    onClick = {
                        if (
                            uiState.title.equals(
                                "Ev",
                                ignoreCase = true
                            ) ||
                            uiState.title.equals(
                                "İş",
                                ignoreCase = true
                            )
                        ) {
                            onTitleChange("")
                        }
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Adres Başlığı *")
                },
                placeholder = {
                    Text(
                        "Ev, İş, Okul, Annem..."
                    )
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange =
                        onCityChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("İl *")
                    },
                    singleLine = true,
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp)
                )

                OutlinedTextField(
                    value = uiState.district,
                    onValueChange =
                        onDistrictChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("İlçe *")
                    },
                    singleLine = true,
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.neighborhood,
                onValueChange =
                    onNeighborhoodChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Mahalle *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.street,
                onValueChange =
                    onStreetChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Cadde / Sokak *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.buildingNo,
                onValueChange =
                    onBuildingNoChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Bina No *")
                },
                singleLine = true,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = uiState.floor,
                    onValueChange =
                        onFloorChange,
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Kat")
                    },
                    singleLine = true,
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp),
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
                    enabled = formEnabled,
                    shape =
                        RoundedCornerShape(15.dp),
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = uiState.addressNote,
                onValueChange =
                    onAddressNoteChange,
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Adres Tarifi (İsteğe bağlı)"
                    )
                },
                placeholder = {
                    Text(
                        "Örn. Mavi kapılı bina, marketin karşısı..."
                    )
                },
                minLines = 2,
                maxLines = 4,
                enabled = formEnabled,
                shape =
                    RoundedCornerShape(15.dp)
            )

            if (!formEnabled) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text =
                        if (
                            uiState.isResolvingAddress
                        ) {
                            "Yeni konumun adres bilgileri hazırlanırken alanlar geçici olarak kilitlendi."
                        } else {
                            "Kaydetme işlemi sürerken adres alanları değiştirilemez."
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }
        }
    }
}

@Composable
private fun EditAddressTitleChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.clickable(
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(50.dp),
        color =
            if (selected) {
                CustomerHomeColors.DeepOlive
            } else {
                CustomerHomeColors.SurfaceSoft
            },
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    width = 1.dp,
                    color =
                        CustomerHomeColors.Outline
                )
            }
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
            style =
                MaterialTheme
                    .typography
                    .labelLarge,
            color =
                when {
                    !enabled ->
                        CustomerHomeColors
                            .TextMuted
                            .copy(alpha = 0.6f)

                    selected ->
                        Color.White

                    else ->
                        CustomerHomeColors
                            .DeepOlive
                },
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun EditAddressPreviewCard(
    fullAddress: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors.OliveSoft
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text =
                    "Güncellenecek Adres",
                style =
                    MaterialTheme
                        .typography
                        .titleSmall,
                fontWeight = FontWeight.Bold,
                color =
                    CustomerHomeColors
                        .DeepOlive
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    fullAddress.ifBlank {
                        "Adres alanlarını tamamladıkça önizleme burada oluşacak."
                    },
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    CustomerHomeColors.Text
            )
        }
    }
}

@Composable
private fun EditDefaultAddressCard(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = {
                    onCheckedChange(!checked)
                }
            ),
        shape = RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors.Surface,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors.Outline
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange =
                    if (enabled) {
                        onCheckedChange
                    } else {
                        null
                    },
                enabled = enabled,
                colors =
                    CheckboxDefaults.colors(
                        checkedColor =
                            CustomerHomeColors
                                .DeepOlive,
                        uncheckedColor =
                            CustomerHomeColors
                                .TextMuted
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(
                    text =
                        "Varsayılan teslimat adresi",
                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        "Bu adresi hesabınızdaki varsayılan adres olarak kullan.",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }
        }
    }
}

@Composable
private fun EditAddressErrorCard(
    message: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color =
            CustomerHomeColors
                .TerracottaSoft
    ) {
        Text(
            text = message,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                CustomerHomeColors.Error,
            fontWeight =
                FontWeight.Medium
        )
    }
}
