package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ReviewResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CustomerReviewsScreen(
    uiState: CustomerReviewsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onDeleteReviewClick: (ReviewResponse) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDeleteReview: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        val snackbarHostState =
            remember {
                SnackbarHostState()
            }

        LaunchedEffect(
            uiState.successMessage
        ) {
            val message =
                uiState.successMessage

            if (!message.isNullOrBlank()) {
                snackbarHostState
                    .showSnackbar(message)

                onMessageShown()
            }
        }

        LaunchedEffect(
            uiState.errorMessage,
            uiState.reviews.isNotEmpty()
        ) {
            val message =
                uiState.errorMessage

            if (
                !message.isNullOrBlank() &&
                uiState.reviews.isNotEmpty()
            ) {
                snackbarHostState
                    .showSnackbar(message)

                onMessageShown()
            }
        }

        uiState.reviewPendingDeletion
            ?.let { review ->
                DeleteReviewDialog(
                    review = review,
                    isDeleting =
                        uiState.deletingReviewId ==
                                review.reviewId,
                    onDismiss =
                        onDismissDeleteDialog,
                    onConfirm =
                        onConfirmDeleteReview
                )
            }

        Scaffold(
            modifier =
                modifier.fillMaxSize(),
            containerColor =
                CustomerHomeColors.Cream,
            snackbarHost = {
                SnackbarHost(
                    hostState =
                        snackbarHostState
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        CustomerHomeColors.Cream
                    )
                    .padding(innerPadding)
            ) {
                CustomerReviewsHeader(
                    reviewCount =
                        uiState.reviews.size,
                    onBackClick =
                        onBackClick
                )

                when {
                    uiState.isLoading -> {
                        CustomerReviewsLoading(
                            modifier =
                                Modifier.weight(1f)
                        )
                    }

                    uiState.errorMessage != null &&
                            uiState.reviews.isEmpty() -> {

                        CustomerReviewsError(
                            message =
                                uiState.errorMessage,
                            onRetryClick =
                                onRetryClick,
                            modifier =
                                Modifier.weight(1f)
                        )
                    }

                    uiState.reviews.isEmpty() -> {
                        CustomerReviewsEmpty(
                            modifier =
                                Modifier.weight(1f)
                        )
                    }

                    else -> {
                        CustomerReviewsContent(
                            reviews =
                                uiState.reviews,
                            deletingReviewId =
                                uiState
                                    .deletingReviewId,
                            onDeleteReviewClick =
                                onDeleteReviewClick,
                            modifier =
                                Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerReviewsHeader(
    reviewCount: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 14.dp
            )
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding =
                PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "←  Hesabıma Dön",
                color =
                    CustomerHomeColors
                        .DeepOlive,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "Değerlendirmelerim",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (reviewCount == 0) {
                            "Siparişlerinize verdiğiniz puanlar ve yorumlar"
                        } else {
                            "$reviewCount değerlendirme"
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted
                )
            }

            Surface(
                modifier =
                    Modifier.size(44.dp),
                shape = CircleShape,
                color =
                    CustomerHomeColors
                        .OliveSoft
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "★",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color =
                            CustomerHomeColors
                                .Gold,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerReviewsContent(
    reviews: List<ReviewResponse>,
    deletingReviewId: Int?,
    onDeleteReviewClick:
        (ReviewResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val averageRating =
        if (reviews.isEmpty()) {
            0.0
        } else {
            reviews
                .map {
                    it.rating
                }
                .average()
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 28.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        item(
            key = "review_summary"
        ) {
            ReviewSummaryCard(
                reviewCount =
                    reviews.size,
                averageRating =
                    averageRating
            )
        }

        item(
            key = "review_section_title"
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        top = 5.dp,
                        bottom = 1.dp
                    )
            ) {
                Text(
                    text =
                        "Geçmiş Değerlendirmeler",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        "Teslim edilen siparişleriniz için bıraktığınız değerlendirmeler.",
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

        items(
            items = reviews,
            key = { review ->
                review.reviewId
            }
        ) { review ->
            CustomerReviewCard(
                review = review,
                isDeleting =
                    deletingReviewId ==
                            review.reviewId,
                isAnyReviewDeleting =
                    deletingReviewId != null,
                onDeleteClick = {
                    onDeleteReviewClick(
                        review
                    )
                }
            )
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    reviewCount: Int,
    averageRating: Double
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        color =
            CustomerHomeColors
                .DeepOlive,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 17.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(18.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier =
                    Modifier.size(62.dp),
                shape = CircleShape,
                color =
                    Color.White.copy(
                        alpha = 0.11f
                    )
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "★",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        color =
                            CustomerHomeColors
                                .Gold,
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
                    text =
                        formatAverageRating(
                            averageRating
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    color = Color.White,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = "Ortalama puan",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        Color.White.copy(
                            alpha = 0.78f
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        buildRatingStars(
                            averageRating
                                .toInt()
                                .coerceIn(
                                    0,
                                    5
                                )
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color =
                        CustomerHomeColors
                            .Gold
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {
                Text(
                    text =
                        reviewCount.toString(),
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color =
                        CustomerHomeColors
                            .Cream,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (reviewCount == 1) {
                            "değerlendirme"
                        } else {
                            "değerlendirme"
                        },
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
                    color =
                        Color.White.copy(
                            alpha = 0.72f
                        )
                )
            }
        }
    }
}

@Composable
private fun CustomerReviewCard(
    review: ReviewResponse,
    isDeleting: Boolean,
    isAnyReviewDeleting: Boolean,
    onDeleteClick: () -> Unit
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
                Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 15.dp,
                        bottom = 14.dp
                    )
            ) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.Top
                ) {
                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                "Sipariş #${review.orderId}",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            color =
                                CustomerHomeColors
                                    .DeepOlive,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                formatReviewDate(
                                    review.createdAt
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

                    ReviewScoreBadge(
                        rating =
                            review.rating
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(13.dp)
                )

                Text(
                    text =
                        buildRatingStars(
                            review.rating
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    color =
                        CustomerHomeColors
                            .Gold
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                HorizontalDivider(
                    color =
                        CustomerHomeColors
                            .Outline
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Yorumunuz",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    color =
                        CustomerHomeColors
                            .SurfaceSoft
                ) {
                    Text(
                        text =
                            review.comment
                                .ifBlank {
                                    "Bu değerlendirmede yazılı yorum bırakmadınız."
                                },
                        modifier =
                            Modifier.padding(
                                horizontal =
                                    13.dp,
                                vertical =
                                    11.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            if (
                                review.comment
                                    .isBlank()
                            ) {
                                CustomerHomeColors
                                    .TextMuted
                            } else {
                                CustomerHomeColors
                                    .Text
                            }
                    )
                }
            }

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                color =
                    CustomerHomeColors
                        .SurfaceSoft
            ) {
                OutlinedButton(
                    onClick =
                        onDeleteClick,
                    enabled =
                        !isAnyReviewDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        )
                        .height(44.dp),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),
                    border =
                        BorderStroke(
                            width = 1.dp,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                                    .copy(
                                        alpha = 0.35f
                                    )
                        )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    19.dp
                                ),
                            strokeWidth = 2.dp,
                            color =
                                CustomerHomeColors
                                    .Terracotta
                        )
                    } else {
                        Text(
                            text =
                                "Değerlendirmeyi Sil",
                            color =
                                CustomerHomeColors
                                    .Terracotta,
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
private fun ReviewScoreBadge(
    rating: Int
) {
    val safeRating =
        rating.coerceIn(
            0,
            5
        )

    Surface(
        shape =
            RoundedCornerShape(50.dp),
        color =
            CustomerHomeColors
                .TerracottaSoft
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(4.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "★",
                color =
                    CustomerHomeColors
                        .Gold,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "$safeRating / 5",
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                color =
                    CustomerHomeColors
                        .Terracotta,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeleteReviewDialog(
    review: ReviewResponse,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isDeleting) {
                onDismiss()
            }
        },
        shape =
            RoundedCornerShape(24.dp),
        containerColor =
            CustomerHomeColors.Surface,
        title = {
            Text(
                text =
                    "Değerlendirmeyi Sil",
                color =
                    CustomerHomeColors
                        .DeepOlive,
                fontWeight =
                    FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text =
                        "Sipariş #${review.orderId} için yaptığınız değerlendirmeyi silmek istediğinizden emin misiniz?",
                    color =
                        CustomerHomeColors
                            .Text,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),
                    color =
                        CustomerHomeColors
                            .TerracottaSoft
                ) {
                    Text(
                        text =
                            "Silinen değerlendirme bu listeden kaldırılır.",
                        modifier =
                            Modifier.padding(
                                12.dp
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
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                CustomerHomeColors
                                    .Terracotta,
                            contentColor =
                                Color.White
                        ),
                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                18.dp
                            ),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Sil",
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text(
                    text = "Vazgeç",
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun CustomerReviewsLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color =
                    CustomerHomeColors
                        .Terracotta
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "Değerlendirmeleriniz yükleniyor...",
                color =
                    CustomerHomeColors
                        .TextMuted,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}

@Composable
private fun CustomerReviewsEmpty(
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(24.dp),
            color =
                CustomerHomeColors
                    .Surface,
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
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 26.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier =
                        Modifier.size(58.dp),
                    shape = CircleShape,
                    color =
                        CustomerHomeColors
                            .OliveSoft
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "☆",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            color =
                                CustomerHomeColors
                                    .DeepOlive
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Henüz değerlendirmeniz yok",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Text(
                    text =
                        "Teslim edilen siparişlerinizi değerlendirdiğinizde puan ve yorumlarınız burada görünecek.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CustomerReviewsError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(24.dp),
            color =
                CustomerHomeColors
                    .Surface,
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
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 24.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier =
                        Modifier.size(54.dp),
                    shape = CircleShape,
                    color =
                        CustomerHomeColors
                            .TerracottaSoft
                ) {
                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,
                            color =
                                CustomerHomeColors
                                    .Error,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(13.dp)
                )

                Text(
                    text =
                        "Değerlendirmeler yüklenemedi",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Button(
                    onClick =
                        onRetryClick,
                    modifier =
                        Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    CustomerHomeColors
                                        .DeepOlive,
                                contentColor =
                                    Color.White
                            ),
                    shape =
                        RoundedCornerShape(
                            16.dp
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

private fun buildRatingStars(
    rating: Int
): String {
    val safeRating =
        rating.coerceIn(
            minimumValue = 0,
            maximumValue = 5
        )

    return "★".repeat(safeRating) +
            "☆".repeat(
                5 - safeRating
            )
}

private fun formatAverageRating(
    averageRating: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.1f / 5",
        averageRating
    )
}

private fun formatReviewDate(
    dateText: String
): String {
    if (dateText.isBlank()) {
        return "-"
    }

    val formatter =
        DateTimeFormatter
            .ofPattern(
                "dd.MM.yyyy HH:mm"
            )

    return runCatching {
        OffsetDateTime
            .parse(dateText)
            .format(formatter)
    }.recoverCatching {
        LocalDateTime
            .parse(dateText)
            .format(formatter)
    }.getOrElse {
        dateText
    }
}
