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
import com.homemadefood.app.ui.location.LocationPermissionSection

@Composable
fun AddAddressScreen(
    uiState: AddressFormUiState,

    onBackClick: () -> Unit,

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
    val formEnabled =
        uiState.selectedLocation != null &&
                !uiState.isSaving &&
                !uiState.isResolvingAddress

    CustomerHomeTheme {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(
                        CustomerHomeColors.Cream
                    )
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
                text = "Yeni Teslimat Adresi",
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    CustomerHomeColors.DeepOlive
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    "Önce teslimat noktanızı haritada belirleyin. " +
                            "Ardından adres bilgilerini kontrol edip eksikleri tamamlayın.",
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

            AddressStepHeader(
                step = "1",
                title = "Teslimat Konumu",
                subtitle =
                    "Siparişin ulaşacağı noktayı haritada seçin."
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            LocationSelectionCard(
                uiState = uiState,
                onSelectLocationClick =
                    onSelectLocationClick
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            AddressStepHeader(
                step = "2",
                title = "Adres Bilgileri",
                subtitle =
                    if (uiState.selectedLocation == null) {
                        "Adres alanlarını açmak için önce konum seçin."
                    } else if (uiState.isResolvingAddress) {
                        "Seçilen konumdan adres bilgileri hazırlanıyor."
                    } else {
                        "Otomatik gelen bilgileri kontrol edip eksikleri tamamlayın."
                    }
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            AddressFormCard(
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

            if (
                uiState.selectedLocation != null
            ) {
                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                AddressPreviewCard(
                    fullAddress =
                        uiState.fullAddress
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            DefaultAddressCard(
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

                AddressErrorCard(
                    message =
                        uiState.errorMessage
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onSaveClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                enabled = uiState.canSave,
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
                                .copy(alpha = 0.36f),
                        disabledContentColor =
                            Color.White
                                .copy(alpha = 0.82f)
                    )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(21.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Adresi Kaydet",
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
                uiState.selectedLocation != null &&
                !uiState.canSave &&
                !uiState.isSaving &&
                !uiState.isResolvingAddress
            ) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Kaydetmek için * işaretli alanları tamamlayın.",
                    modifier =
                        Modifier.fillMaxWidth(),
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
private fun AddressStepHeader(
    step: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(11.dp),
        verticalAlignment =
            Alignment.Top
    ) {
        Surface(
            modifier =
                Modifier.size(38.dp),
            shape = CircleShape,
            color =
                CustomerHomeColors
                    .DeepOlive
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
            modifier =
                Modifier.weight(1f)
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
private fun LocationSelectionCard(
    uiState: AddressFormUiState,
    onSelectLocationClick:
        () -> Unit
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(17.dp)
        ) {
            LocationPermissionSection()

            Spacer(
                modifier = Modifier.height(15.dp)
            )

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
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(
                                        20.dp
                                    ),
                                strokeWidth = 2.dp,
                                color =
                                    CustomerHomeColors
                                        .Terracotta
                            )

                            Text(
                                text =
                                    "Konum seçildi, adres bilgileri bulunuyor...",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    CustomerHomeColors
                                        .Text
                            )
                        }
                    }
                }

                uiState.selectedLocation == null -> {
                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(15.dp),
                        color =
                            CustomerHomeColors
                                .SurfaceSoft
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    13.dp
                                )
                        ) {
                            Text(
                                text =
                                    "Henüz teslimat noktası seçilmedi",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                fontWeight =
                                    FontWeight
                                        .SemiBold,
                                color =
                                    CustomerHomeColors
                                        .DeepOlive
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )

                            Text(
                                text =
                                    "Haritada evinizin veya teslimat almak istediğiniz noktanın üzerine dokunun.",
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

                else -> {
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
                                Modifier.padding(
                                    13.dp
                                )
                        ) {
                            Text(
                                text =
                                    "✓ Teslimat noktası seçildi",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,
                                color =
                                    CustomerHomeColors
                                        .DeepOlive,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            if (
                                !uiState
                                    .locationLookupMessage
                                    .isNullOrBlank()
                            ) {
                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            5.dp
                                        )
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
                                            .TextMuted
                                )
                            }
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
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                enabled =
                    !uiState.isSaving &&
                            !uiState
                                .isResolvingAddress,
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
                            uiState
                                .selectedLocation ==
                            null
                        ) {
                            "Haritada Konum Seç"
                        } else {
                            "Konumu Değiştir"
                        },
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight
                            .SemiBold
                )
            }
        }
    }
}

@Composable
private fun AddressFormCard(
    uiState: AddressFormUiState,
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
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
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
            modifier =
                Modifier
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
                AddressTitleChip(
                    text = "Ev",
                    selected =
                        uiState.title
                            .equals(
                                "Ev",
                                ignoreCase = true
                            ),
                    enabled = formEnabled,
                    onClick = {
                        onTitleChange("Ev")
                    }
                )

                AddressTitleChip(
                    text = "İş",
                    selected =
                        uiState.title
                            .equals(
                                "İş",
                                ignoreCase = true
                            ),
                    enabled = formEnabled,
                    onClick = {
                        onTitleChange("İş")
                    }
                )

                AddressTitleChip(
                    text = "Diğer",
                    selected =
                        uiState.title
                            .isNotBlank() &&
                                !uiState.title
                                    .equals(
                                        "Ev",
                                        ignoreCase = true
                                    ) &&
                                !uiState.title
                                    .equals(
                                        "İş",
                                        ignoreCase = true
                                    ),
                    enabled = formEnabled,
                    onClick = {
                        if (
                            uiState.title
                                .equals(
                                    "Ev",
                                    ignoreCase = true
                                ) ||
                            uiState.title
                                .equals(
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
                onValueChange =
                    onTitleChange,
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
                    value =
                        uiState.district,
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
                value =
                    uiState.buildingNo,
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
                value =
                    uiState.addressNote,
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
                            uiState
                                .isResolvingAddress
                        ) {
                            "Adres alanları konum bilgileri hazırlanırken geçici olarak kilitlendi."
                        } else {
                            "Bu alanları düzenlemek için önce haritadan teslimat konumunu seçin."
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
private fun AddressTitleChip(
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
        shape =
            RoundedCornerShape(50.dp),
        color =
            when {
                selected ->
                    CustomerHomeColors
                        .DeepOlive

                else ->
                    CustomerHomeColors
                        .SurfaceSoft
            },
        border =
            if (selected) {
                null
            } else {
                BorderStroke(
                    width = 1.dp,
                    color =
                        CustomerHomeColors
                            .Outline
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
private fun AddressPreviewCard(
    fullAddress: String
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors
                .OliveSoft
    ) {
        Column(
            modifier =
                Modifier.padding(15.dp)
        ) {
            Text(
                text = "Kaydedilecek Adres",
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
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    fullAddress.ifBlank {
                        "Adres bilgilerini tamamladıkça önizleme burada oluşacak."
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
private fun DefaultAddressCard(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange:
        (Boolean) -> Unit
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    onClick = {
                        onCheckedChange(
                            !checked
                        )
                    }
                ),
        shape =
            RoundedCornerShape(18.dp),
        color =
            CustomerHomeColors.Surface,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CustomerHomeColors
                        .Outline
            )
    ) {
        Row(
            modifier =
                Modifier
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
                modifier =
                    Modifier
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
                    modifier =
                        Modifier.height(2.dp)
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
private fun AddressErrorCard(
    message: String
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(16.dp),
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
                CustomerHomeColors
                    .Error,
            fontWeight =
                FontWeight.Medium
        )
    }
}