package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.CategoryResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCategorySelector(
    categories: List<CategoryResponse>,
    selectedCategoryId: Int?,
    selectedCategoryName: String,
    isLoading: Boolean,
    errorMessage: String?,
    onCategorySelected: (Int) -> Unit,
    onRetryClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedName =
        categories
            .firstOrNull {
                it.id == selectedCategoryId
            }
            ?.name
            ?: selectedCategoryName

    val canOpen =
        enabled &&
                !isLoading &&
                categories.isNotEmpty()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (canOpen) {
                    expanded = !expanded
                }
            }
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                enabled = enabled,
                label = {
                    Text("Kategori *")
                },
                placeholder = {
                    Text("Kategori seçin")
                },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded = expanded
                            )
                    }
                },
                supportingText = {
                    when {
                        isLoading -> {
                            Text("Kategoriler yükleniyor...")
                        }

                        !errorMessage.isNullOrBlank() -> {
                            Text(errorMessage)
                        }

                        categories.isEmpty() -> {
                            Text("Aktif kategori bulunamadı.")
                        }

                        selectedName.isBlank() -> {
                            Text("Listeden bir kategori seçin.")
                        }

                        else -> {
                            Text("Seçilen kategori: $selectedName")
                        }
                    }
                },
                isError =
                    !errorMessage.isNullOrBlank()
            )

            ExposedDropdownMenu(
                expanded = expanded && canOpen,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme
                                        .typography
                                        .bodyLarge
                                )

                                if (
                                    category.description
                                        .isNotBlank()
                                ) {
                                    Text(
                                        text =
                                            category.description,
                                        style = MaterialTheme
                                            .typography
                                            .bodySmall
                                    )
                                }
                            }
                        },
                        onClick = {
                            expanded = false
                            onCategorySelected(
                                category.id
                            )
                        },
                        contentPadding =
                            ExposedDropdownMenuDefaults
                                .ItemContentPadding
                    )
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Spacer(
                modifier = Modifier.height(2.dp)
            )

            TextButton(
                onClick = onRetryClick,
                enabled = enabled && !isLoading
            ) {
                Text("Kategorileri Tekrar Yükle")
            }
        }
    }
}