package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.homemadefood.app.R
import com.homemadefood.app.data.model.CategoryResponse

@Composable
fun CustomerHomeSearchAndCategorySection(
    searchQuery: String,
    categories: List<CategoryResponse>,
    selectedCategoryId: Int?,
    isCategoriesLoading: Boolean,
    categoryErrorMessage: String?,
    isStorefrontsLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
    onClearFiltersClick: () -> Unit,
    onRetryCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            shape =
                RoundedCornerShape(
                    20.dp
                ),
            color =
                CustomerHomeColors
                    .Surface,
            border =
                BorderStroke(
                    width = 1.dp,
                    color =
                        CustomerHomeColors
                            .Outline
                ),
            shadowElevation = 0.dp
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 14.dp,
                            end = 7.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_customer_home_search
                        ),
                    contentDescription = null,
                    tint =
                        CustomerHomeColors
                            .TextMuted,
                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(10.dp)
                )

                BasicTextField(
                    value = searchQuery,
                    onValueChange =
                        onSearchQueryChange,
                    modifier =
                        Modifier.weight(1f),
                    singleLine = true,
                    textStyle =
                        MaterialTheme
                            .typography
                            .bodyMedium
                            .merge(
                                TextStyle(
                                    color =
                                        CustomerHomeColors
                                            .Text
                                )
                            ),
                    cursorBrush =
                        SolidColor(
                            CustomerHomeColors
                                .DeepOlive
                        ),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction =
                                ImeAction.Search
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onSearch = {
                                onSearchClick()
                            }
                        ),
                    decorationBox = {
                            innerTextField ->

                        if (
                            searchQuery.isBlank()
                        ) {
                            Text(
                                text =
                                    "İşletme veya şehir ara",
                                maxLines = 1,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    CustomerHomeColors
                                        .TextMuted
                            )
                        }

                        innerTextField()
                    }
                )

                Spacer(
                    modifier =
                        Modifier.size(7.dp)
                )

                Surface(
                    modifier =
                        Modifier.size(34.dp),
                    shape = CircleShape,
                    color =
                        if (isStorefrontsLoading) {
                            CustomerHomeColors
                                .Terracotta
                                .copy(alpha = 0.48f)
                        } else {
                            CustomerHomeColors
                                .Terracotta
                        }
                ) {
                    IconButton(
                        onClick =
                            onSearchClick,
                        enabled =
                            !isStorefrontsLoading
                    ) {
                        if (
                            isStorefrontsLoading
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(
                                        16.dp
                                    ),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                painter =
                                    painterResource(
                                        id =
                                            R.drawable
                                                .ic_customer_home_search
                                    ),
                                contentDescription =
                                    "Ara",
                                tint = Color.White,
                                modifier =
                                    Modifier.size(
                                        16.dp
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "Kategoriler",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    CustomerHomeColors
                        .DeepOlive
            )

            if (
                searchQuery.isNotBlank() ||
                selectedCategoryId != null
            ) {
                TextButton(
                    onClick =
                        onClearFiltersClick
                ) {
                    Text(
                        text = "Temizle",
                        color =
                            CustomerHomeColors
                                .Terracotta,
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        when {
            isCategoriesLoading -> {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 10.dp
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
                                .DeepOlive
                    )

                    Text(
                        text =
                            "Kategoriler yükleniyor...",
                        modifier =
                            Modifier.padding(
                                start = 10.dp
                            ),
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

            categories.isEmpty() &&
                    categoryErrorMessage != null -> {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            categoryErrorMessage,
                        modifier =
                            Modifier.weight(1f),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    TextButton(
                        onClick =
                            onRetryCategoriesClick
                    ) {
                        Text(
                            "Tekrar Dene"
                        )
                    }
                }
            }

            categories.isEmpty() -> {
                Text(
                    text =
                        "Henüz aktif kategori bulunmuyor.",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }

            else -> {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            9.dp
                        )
                ) {
                    HomeCategoryPill(
                        text = "Tümü",
                        selected =
                            selectedCategoryId ==
                                    null,
                        onClick = {
                            onCategoryClick(null)
                        }
                    )

                    categories.forEach {
                            category ->

                        HomeCategoryPill(
                            text =
                                category.name,
                            selected =
                                selectedCategoryId ==
                                        category.id,
                            onClick = {
                                onCategoryClick(
                                    category.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.clickable(
                onClick = onClick
            ),
        shape =
            RoundedCornerShape(
                50.dp
            ),
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
                width = 1.dp,
                color =
                    if (selected) {
                        CustomerHomeColors
                            .DeepOlive
                    } else {
                        CustomerHomeColors
                            .Outline
                    }
            ),
        shadowElevation =
            if (selected) {
                2.dp
            } else {
                0.dp
            }
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 17.dp,
                    vertical = 10.dp
                ),
            color =
                if (selected) {
                    Color.White
                } else {
                    CustomerHomeColors
                        .DeepOlive
                },
            style =
                MaterialTheme
                    .typography
                    .labelLarge,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
        )
    }
}