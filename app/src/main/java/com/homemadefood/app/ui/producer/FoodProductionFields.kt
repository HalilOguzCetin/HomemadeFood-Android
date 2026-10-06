package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.FoodProductionOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodProductionFields(
    selectedUnitType: String,
    dailyCapacity: String,
    minimumOrderLeadTimeMinutes: String,
    selectedAllergenCodes: Set<String>,
    onUnitTypeSelected: (String) -> Unit,
    onDailyCapacityChange: (String) -> Unit,
    onMinimumOrderLeadTimeChange: (String) -> Unit,
    onAllergenToggle: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var unitMenuExpanded by remember {
        mutableStateOf(false)
    }

    val selectedUnitName =
        FoodProductionOptions
            .unitDisplayName(selectedUnitType)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Üretim ve Satış Bilgileri",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text =
                "Yemeğin nasıl satıldığını, günlük üretim kapasitesini ve siparişin ne kadar önceden verilmesi gerektiğini belirtin.",
            style = MaterialTheme.typography.bodySmall
        )

        ExposedDropdownMenuBox(
            expanded = unitMenuExpanded,
            onExpandedChange = {
                if (enabled) {
                    unitMenuExpanded = !unitMenuExpanded
                }
            }
        ) {
            OutlinedTextField(
                value = selectedUnitName,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                enabled = enabled,
                label = {
                    Text("Satış Birimi *")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults
                        .TrailingIcon(
                            expanded = unitMenuExpanded
                        )
                },
                supportingText = {
                    Text(
                        "Örn. porsiyon, tepsi, adet, kilogram veya paket."
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = unitMenuExpanded && enabled,
                onDismissRequest = {
                    unitMenuExpanded = false
                }
            ) {
                FoodProductionOptions
                    .unitTypes
                    .forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option.displayName)
                            },
                            onClick = {
                                unitMenuExpanded = false
                                onUnitTypeSelected(
                                    option.backendValue
                                )
                            },
                            contentPadding =
                                ExposedDropdownMenuDefaults
                                    .ItemContentPadding
                        )
                    }
            }
        }

        OutlinedTextField(
            value = dailyCapacity,
            onValueChange = onDailyCapacityChange,
            label = {
                Text("Günlük Kapasite *")
            },
            supportingText = {
                Text(
                    "Bu yemekten bir günde satabileceğiniz maksimum ${selectedUnitName.lowercase()} miktarı."
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = minimumOrderLeadTimeMinutes,
            onValueChange = onMinimumOrderLeadTimeChange,
            label = {
                Text("Minimum Sipariş Ön Süresi *")
            },
            suffix = {
                Text("dk")
            },
            supportingText = {
                Text(
                    "Siparişin hazırlanabilmesi için en az kaç dakika önceden verilmesi gerektiğini yazın. Hemen hazırlanabilen ürün için 0 girebilirsiniz."
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Alerjenler",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text =
                "Yemeğin içerdiği alerjenleri işaretleyin. Bu alan İçindekiler metninden ayrı tutulur.",
            style = MaterialTheme.typography.bodySmall
        )

        FoodProductionOptions
            .allergens
            .chunked(2)
            .forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    rowOptions.forEach { option ->
                        FilterChip(
                            selected =
                                selectedAllergenCodes
                                    .any {
                                        it.equals(
                                            option.backendValue,
                                            ignoreCase = true
                                        )
                                    },
                            onClick = {
                                onAllergenToggle(
                                    option.backendValue
                                )
                            },
                            enabled = enabled,
                            label = {
                                Text(option.displayName)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowOptions.size == 1) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
    }
}
