package com.homemadefood.app.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDeliveryAddressPicker(
    isLoading: Boolean,
    addresses: List<AddressResponse>,
    selectedAddress: AddressResponse?,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onAddressSelected: (Int) -> Unit,
    onAddAddressClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddressSheet by
    rememberSaveable {
        mutableStateOf(false)
    }

    when {
        isLoading &&
                selectedAddress == null -> {

            Card(
                modifier =
                    modifier.fillMaxWidth()
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(22.dp),
                        strokeWidth = 2.dp
                    )

                    Column(
                        modifier =
                            Modifier.padding(
                                start = 12.dp
                            )
                    ) {
                        Text(
                            text = "Teslimat adresi",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium
                        )

                        Text(
                            text =
                                "Adresleriniz yükleniyor...",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )
                    }
                }
            }
        }

        selectedAddress != null -> {
            Card(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = !isLoading,
                            onClick = {
                                showAddressSheet = true
                            }
                        )
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    end = 12.dp
                                )
                    ) {
                        Text(
                            text =
                                "Teslimat adresi",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Text(
                                text =
                                    selectedAddress.title,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            if (
                                selectedAddress.isDefault
                            ) {
                                Text(
                                    text =
                                        "  • Varsayılan",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                deliveryAddressSummary(
                                    selectedAddress
                                ),

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,

                            maxLines = 2,

                            overflow =
                                TextOverflow.Ellipsis
                        )

                        if (
                            !errorMessage.isNullOrBlank()
                        ) {
                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                    "Adresler yenilenemedi. Tekrar deneyebilirsiniz.",

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .error
                            )
                        }
                    }

                    Text(
                        text = "⌄",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge
                    )
                }
            }

            if (
                !errorMessage.isNullOrBlank()
            ) {
                TextButton(
                    onClick = onRetryClick,
                    enabled = !isLoading
                ) {
                    Text(
                        "Adresleri Tekrar Yükle"
                    )
                }
            }
        }

        !errorMessage.isNullOrBlank() -> {
            Card(
                modifier =
                    modifier.fillMaxWidth()
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Teslimat adresi",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text = errorMessage,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Adresleri Tekrar Yükle"
                        )
                    }
                }
            }
        }

        addresses.isEmpty() -> {
            Card(
                modifier =
                    modifier.fillMaxWidth()
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Teslimat adresi",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Henüz kayıtlı teslimat adresiniz yok.",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(
                        onClick =
                            onAddAddressClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Yeni Adres Ekle"
                        )
                    }
                }
            }
        }
    }

    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddressSheet = false
            }
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 24.dp
                        )
            ) {
                Text(
                    text =
                        "Teslimat adresini seç",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Bu seçim yalnız şu anki teslimat adresinizi değiştirir. Varsayılan adres ayarınız değişmez.",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                addresses.forEach {
                        address ->

                    DeliveryAddressOption(
                        address =
                            address,

                        selected =
                            selectedAddress
                                ?.id ==
                                    address.id,

                        onClick = {
                            onAddressSelected(
                                address.id
                            )

                            showAddressSheet =
                                false
                        }
                    )
                }

                HorizontalDivider(
                    modifier =
                        Modifier.padding(
                            vertical = 10.dp
                        )
                )

                TextButton(
                    onClick = {
                        showAddressSheet =
                            false

                        onAddAddressClick()
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        "+ Yeni adres ekle"
                    )
                }

                TextButton(
                    onClick = {
                        showAddressSheet =
                            false

                        onManageAddressesClick()
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Adreslerimi yönet"
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }
        }
    }
}

@Composable
private fun DeliveryAddressOption(
    address: AddressResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
                .padding(
                    vertical = 10.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 8.dp
                    )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = address.title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                if (address.isDefault) {
                    Text(
                        text =
                            "  • Varsayılan",

                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    deliveryAddressSummary(
                        address
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                maxLines = 2,

                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

private fun deliveryAddressSummary(
    address: AddressResponse
): String {
    val locationSummary =
        listOf(
            address.neighborhood,
            address.district,
            address.city
        )
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .joinToString(
                " / "
            )

    if (locationSummary.isNotBlank()) {
        return locationSummary
    }

    return address.fullAddress
        .trim()
        .ifBlank {
            "Adres ayrıntısı bulunmuyor"
        }
}