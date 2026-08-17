package com.homemadefood.app.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ReviewResponse
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppLoadingState
import com.homemadefood.app.ui.components.AppMessageType
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProducerReviewsScreen(
    uiState: ProducerReviewsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            AppLoadingState(
                modifier = modifier,
                message = "Değerlendirmeler yükleniyor..."
            )
        }

        uiState.errorMessage != null -> {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text("← Üretici Paneline Dön")
                }

                AppErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        else -> {
            ProducerReviewsContent(
                uiState = uiState,
                onBackClick = onBackClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ProducerReviewsContent(
    uiState: ProducerReviewsUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        item {
            TextButton(
                onClick = onBackClick
            ) {
                Text("← Üretici Paneline Dön")
            }
        }

        item {
            Text(
                text = "Değerlendirmelerim",
                style =
                    MaterialTheme.typography
                        .headlineMedium
            )
        }

        item {
            Text(
                text =
                    "Müşterilerinizin teslim edilen siparişler için yaptığı değerlendirmeler.",
                style =
                    MaterialTheme.typography
                        .bodyLarge
            )
        }

        item {
            ProducerReviewSummaryCard(
                totalReviewCount =
                    uiState.totalReviewCount,

                averageRating =
                    uiState.averageRating
            )
        }

        if (uiState.reviews.isEmpty()) {
            item {
                AppInlineMessage(
                    message =
                        "Henüz değerlendirme bulunmuyor. Müşteriler teslim edilen siparişleri değerlendirdiğinde yorumlar burada görünecek.",
                    type = AppMessageType.Info
                )
            }
        } else {
            items(
                items = uiState.reviews,
                key = { review ->
                    review.reviewId
                }
            ) { review ->
                ProducerReviewCard(
                    review = review
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun ProducerReviewSummaryCard(
    totalReviewCount: Int,
    averageRating: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Değerlendirme Özeti",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ortalama Puan",
                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (totalReviewCount == 0) {
                                "-"
                            } else {
                                formatAverageRating(
                                    averageRating
                                ) + " / 5"
                            },

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        color =
                            MaterialTheme.colorScheme
                                .primary
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {
                    Text(
                        text =
                            "Toplam Değerlendirme",

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            totalReviewCount.toString(),

                        style =
                            MaterialTheme.typography
                                .headlineSmall
                    )
                }
            }

            if (totalReviewCount > 0) {
                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text =
                        buildRatingStars(
                            averageRating
                                .toInt()
                                .coerceIn(0, 5)
                        ),

                    style =
                        MaterialTheme.typography
                            .headlineMedium,

                    color =
                        MaterialTheme.colorScheme
                            .primary
                )
            }
        }
    }
}

@Composable
private fun ProducerReviewCard(
    review: ReviewResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            review.customerFullName
                                .ifBlank {
                                    "Müşteri"
                                },

                        style =
                            MaterialTheme.typography
                                .titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            "Sipariş #${review.orderId}",

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )
                }

                Text(
                    text =
                        "${review.rating} / 5",

                    style =
                        MaterialTheme.typography
                            .titleMedium,

                    color =
                        MaterialTheme.colorScheme
                            .primary
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    buildRatingStars(
                        review.rating
                    ),

                style =
                    MaterialTheme.typography
                        .headlineSmall,

                color =
                    MaterialTheme.colorScheme
                        .primary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    if (review.comment.isBlank()) {
                        "Müşteri yazılı yorum bırakmadı."
                    } else {
                        review.comment
                    },

                style =
                    MaterialTheme.typography
                        .bodyLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Değerlendirme tarihi: " +
                            formatReviewDate(
                                review.createdAt
                            ),

                style =
                    MaterialTheme.typography
                        .bodySmall
            )
        }
    }
}

private fun buildRatingStars(
    rating: Int
): String {
    val safeRating =
        rating.coerceIn(
            minimumValue = 0,
            maximumValue = 5
        )

    return "★".repeat(safeRating) +
            "☆".repeat(5 - safeRating)
}

private fun formatAverageRating(
    averageRating: Double
): String {
    return String.format(
        Locale.getDefault(),
        "%.1f",
        averageRating
    )
}

private fun formatReviewDate(
    dateText: String
): String {
    if (dateText.isBlank()) {
        return "-"
    }

    val outputFormatter =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm"
        )

    return runCatching {
        OffsetDateTime
            .parse(dateText)
            .format(outputFormatter)
    }.recoverCatching {
        LocalDateTime
            .parse(dateText)
            .format(outputFormatter)
    }.getOrElse {
        dateText
    }
}