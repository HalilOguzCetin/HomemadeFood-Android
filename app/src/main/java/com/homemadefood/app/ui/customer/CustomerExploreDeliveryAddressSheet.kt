package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.AddressResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerExploreDeliveryAddressSheet(
    addresses: List<AddressResponse>,
    selectedAddressId: Int?,
    onDismiss: () -> Unit,
    onAddressSelected: (Int) -> Unit,
    onAddAddressClick: () -> Unit,
    onManageAddressesClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest =
            onDismiss,
        containerColor =
            CustomerHomeColors
                .Cream
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 18.dp
                    )
        ) {
            Text(
                text =
                    "Teslimat adresini seç",
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
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            Text(
                text =
                    "Keşfet sonuçları seçtiğin adresin 30 km çevresine göre yenilenir.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    CustomerHomeColors
                        .TextMuted
            )

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(
                            1f,
                            fill = false
                        ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {
                items(
                    items =
                        addresses,
                    key = {
                            address ->
                        address.id
                    }
                ) { address ->
                    ExploreAddressOption(
                        address =
                            address,

                        selected =
                            selectedAddressId ==
                                    address.id,

                        onClick = {
                            onAddressSelected(
                                address.id
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            HorizontalDivider(
                color =
                    CustomerHomeColors
                        .Outline
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                TextButton(
                    onClick =
                        onAddAddressClick
                ) {
                    Text(
                        text =
                            "+ Yeni Adres Ekle",
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        fontWeight =
                            FontWeight
                                .SemiBold
                    )
                }

                TextButton(
                    onClick =
                        onManageAddressesClick
                ) {
                    Text(
                        text =
                            "Adresleri Yönet",
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
}

@Composable
private fun ExploreAddressOption(
    address: AddressResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                16.dp
            ),
        color =
            if (selected) {
                CustomerHomeColors
                    .OliveSoft
            } else {
                CustomerHomeColors
                    .Surface
            },
        border =
            BorderStroke(
                width =
                    1.dp,
                color =
                    if (selected) {
                        CustomerHomeColors
                            .DeepOlive
                    } else {
                        CustomerHomeColors
                            .Outline
                    }
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal =
                        12.dp,
                    vertical =
                        11.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .size(
                            22.dp
                        ),
                contentAlignment =
                    Alignment.Center
            ) {
                Surface(
                    modifier =
                        Modifier.size(
                            20.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        if (selected) {
                            CustomerHomeColors
                                .DeepOlive
                        } else {
                            CustomerHomeColors
                                .Surface
                        },
                    border =
                        BorderStroke(
                            1.dp,
                            if (selected) {
                                CustomerHomeColors
                                    .DeepOlive
                            } else {
                                CustomerHomeColors
                                    .TextMuted
                            }
                        )
                ) {
                    if (selected) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Surface(
                                modifier =
                                    Modifier.size(
                                        7.dp
                                    ),
                                shape =
                                    CircleShape,
                                color =
                                    CustomerHomeColors
                                        .Cream
                            ) {}
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            address.title,
                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            CustomerHomeColors
                                .Text,
                        maxLines =
                            1,
                        overflow =
                            TextOverflow
                                .Ellipsis
                    )

                    if (address.isDefault) {
                        Spacer(
                            modifier =
                                Modifier.width(
                                    7.dp
                                )
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(
                                    50.dp
                                ),
                            color =
                                CustomerHomeColors
                                    .TerracottaSoft
                        ) {
                            Text(
                                text =
                                    "Varsayılan",
                                modifier =
                                    Modifier.padding(
                                        horizontal =
                                            7.dp,
                                        vertical =
                                            2.dp
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    CustomerHomeColors
                                        .Terracotta,
                                fontWeight =
                                    FontWeight
                                        .SemiBold
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )

                Text(
                    text =
                        buildExploreSheetAddressSummary(
                            address
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines =
                        2,
                    overflow =
                        TextOverflow
                            .Ellipsis
                )

                if (selected) {
                    Spacer(
                        modifier =
                            Modifier.height(
                                5.dp
                            )
                    )

                    Text(
                        text =
                            "Aktif teslimat adresi",
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            CustomerHomeColors
                                .DeepOlive,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun buildExploreSheetAddressSummary(
    address: AddressResponse
): String {
    val location =
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
                " • "
            )

    return location
        .ifBlank {
            address.fullAddress
        }
}