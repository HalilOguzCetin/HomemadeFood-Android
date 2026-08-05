package com.homemadefood.app.ui.customer

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ReviewResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
            snackbarHostState.showSnackbar(
                message = message
            )

            onMessageShown()
        }
    }

    LaunchedEffect(
        uiState.errorMessage
    ) {
        val message =
            uiState.errorMessage

        if (
            !message.isNullOrBlank() &&
            uiState.reviews.isNotEmpty()
        ) {
            snackbarHostState.showSnackbar(
                message = message
            )

            onMessageShown()
        }
    }

                    Scaffold(
                        modifier = modifier.fillMaxSize(),

                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState
                            )
                        }
                    ) { innerPadding ->

                        when {
                            uiState.isLoading &&
                                    uiState.reviews.isEmpty() -> {

                                CustomerReviewsLoadingContent(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                )
                            }

                            uiState.errorMessage != null &&
                                    uiState.reviews.isEmpty() -> {

                                CustomerReviewsErrorContent(
                                    message =
                                        uiState.errorMessage,

                                    onRetryClick =
                                        onRetryClick,

                                    onBackClick =
                                        onBackClick,

                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                )
                            }

                            else -> {
                                CustomerReviewsContent(
                                    uiState = uiState,

                                    onBackClick =
                                        onBackClick,

                                    onRetryClick =
                                        onRetryClick,

                                    onDeleteReviewClick =
                                        onDeleteReviewClick,

                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                )
                            }
                        }
                    }

                    val reviewPendingDeletion =
                        uiState.reviewPendingDeletion

                    if (reviewPendingDeletion != null) {
                        DeleteReviewConfirmationDialog(
                            review =
                                reviewPendingDeletion,

                            isDeleting =
                                uiState.deletingReviewId ==
                                        reviewPendingDeletion.reviewId,

                            onDismiss =
                                onDismissDeleteDialog,

                            onConfirm =
                                onConfirmDeleteReview
                        )
                    }
                }

        @Composable
        private fun CustomerReviewsLoadingContent(
            modifier: Modifier = Modifier
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {
                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Değerlendirmeleriniz yükleniyor..."
                )
            }
        }

        @Composable
        private fun CustomerReviewsErrorContent(
            message: String,
            onRetryClick: () -> Unit,
            onBackClick: () -> Unit,
            modifier: Modifier = Modifier
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {
                Text(
                    text = "Değerlendirmeler alınamadı",

                    style =
                        MaterialTheme.typography
                            .titleLarge,

                    color =
                        MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = message,

                    style =
                        MaterialTheme.typography
                            .bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ana Sayfaya Dön")
                }
            }
        }

        @Composable
        private fun CustomerReviewsContent(
            uiState: CustomerReviewsUiState,
            onBackClick: () -> Unit,
            onRetryClick: () -> Unit,
            onDeleteReviewClick:
                (ReviewResponse) -> Unit,
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
                        Text("← Ana Sayfaya Dön")
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
                            "Daha önce verdiğiniz puanları ve yorumları buradan görüntüleyebilirsiniz.",

                        style =
                            MaterialTheme.typography
                                .bodyLarge
                    )
                }

                if (
                    uiState.isLoading &&
                    uiState.reviews.isNotEmpty()
                ) {
                    item {
                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                if (uiState.reviews.isEmpty()) {
                    item {
                        EmptyCustomerReviewsCard(
                            onRetryClick =
                                onRetryClick
                        )
                    }
                } else {
                    items(
                        items = uiState.reviews,

                        key = { review ->
                            review.reviewId
                        }
                    ) { review ->

                        CustomerReviewCard(
                            review = review,

                            isDeleting =
                                uiState.deletingReviewId ==
                                        review.reviewId,

                            onDeleteClick = {
                                onDeleteReviewClick(
                                    review
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        @Composable
        private fun EmptyCustomerReviewsCard(
            onRetryClick: () -> Unit
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text =
                            "Henüz değerlendirme yapmadınız.",

                        style =
                            MaterialTheme.typography
                                .titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Teslim edilmiş siparişlerinizi sipariş detayından değerlendirebilirsiniz.",

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    TextButton(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Listeyi Yenile")
                    }
                }
            }
        }

        @Composable
        private fun CustomerReviewCard(
            review: ReviewResponse,
            isDeleting: Boolean,
            onDeleteClick: () -> Unit
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text =
                                    review.businessName
                                        .ifBlank {
                                            "İşletme"
                                        },

                                style =
                                    MaterialTheme.typography
                                        .titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
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
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            buildReviewStars(
                                rating = review.rating
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
                                "Yazılı yorum bırakılmadı."
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
                                    formatCustomerReviewDate(
                                        review.createdAt
                                    ),

                        style =
                            MaterialTheme.typography
                                .bodySmall
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    TextButton(
                        onClick = onDeleteClick,
                        enabled = !isDeleting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(22.dp),

                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text =
                                    "Değerlendirmeyi Sil",

                                color =
                                    MaterialTheme.colorScheme
                                        .error
                            )
                        }
                    }
                }
            }
        }

        @Composable
        private fun DeleteReviewConfirmationDialog(
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

                title = {
                    Text("Değerlendirmeyi Sil")
                },

                text = {
                    Column {
                        Text(
                            text =
                                "Bu değerlendirmeyi silmek istediğinize emin misiniz?"
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                review.businessName
                                    .ifBlank {
                                        "İşletme"
                                    },

                            style =
                                MaterialTheme.typography
                                    .titleSmall
                        )

                        Text(
                            text =
                                "Sipariş #${review.orderId}"
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "Bu işlem geri alınamaz.",

                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }
                },

                confirmButton = {
                    TextButton(
                        onClick = onConfirm,
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.height(20.dp),

                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sil",
                                color =
                                    MaterialTheme.colorScheme
                                        .error
                            )
                        }
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isDeleting
                    ) {
                        Text("Vazgeç")
                    }
                }
            )
        }

                private fun buildReviewStars(
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

                private fun formatCustomerReviewDate(
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