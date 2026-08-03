package com.homemadefood.app.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.homemadefood.app.data.model.OrderStatus
import androidx.compose.material3.OutlinedTextField
import com.homemadefood.app.data.model.ReviewResponse
@Composable
fun OrderDetailScreen(
    uiState: OrderDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCancelOrderClick: () -> Unit,
    onShowReviewFormClick: () -> Unit,
    onHideReviewFormClick: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember {
        mutableStateOf(false)
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
            },

            title = {
                Text("Siparişi İptal Et")
            },

            text = {
                Text(
                    "Bu siparişi iptal etmek istediğinizden emin misiniz?"
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelOrderClick()
                    }
                ) {
                    Text("Evet, İptal Et")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                    }
                ) {
                    Text("Vazgeç")
                }
            }
        )
    }

    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.order == null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text("← Siparişlerime Dön")
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text =
                        uiState.errorMessage
                            ?: "Sipariş bilgisi bulunamadı.",
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tekrar Dene")
                }
            }
        }

        else -> {
            val order = uiState.order
            val orderStatus =
                order.orderStatus

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                item {
                    TextButton(
                        onClick = onBackClick,
                        enabled = !uiState.isCancelling
                    ) {
                        Text("← Siparişlerime Dön")
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sipariş #${order.orderId}",
                            style =
                                MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text =
                                orderStatus.displayName,

                            color =
                                getOrderStatusColor(
                                    orderStatus
                                ),

                            style =
                                MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    if (!uiState.actionMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.actionMessage,
                            color =
                                MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.errorMessage,
                            color =
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sipariş Bilgileri",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            OrderDetailInformationRow(
                                title = "Üretici",
                                value = order.businessName
                            )

                            OrderDetailInformationRow(
                                title = "Toplam tutar",
                                value =
                                    formatOrderPrice(
                                        order.totalPrice
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Ödeme",
                                value =
                                    translatePaymentMethod(
                                        order.paymentMethod
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Sipariş tarihi",
                                value =
                                    formatOrderDate(
                                        order.createdAt
                                    )
                            )

                            OrderDetailInformationRow(
                                title = "Son güncelleme",
                                value =
                                    formatOrderDate(
                                        order.statusUpdatedAt
                                    )
                            )

                            if (
                                order.recommendationSearchId != null
                            ) {
                                OrderDetailInformationRow(
                                    title = "Uygunluk puanı",
                                    value =
                                        String.format(
                                            Locale("tr", "TR"),
                                            "%.2f",
                                            order.suitabilityScore
                                        )
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Teslimat Adresi",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    order.deliveryAddressTitle,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Text(
                                text = order.deliveryAddress,
                                style =
                                    MaterialTheme.typography.bodyLarge
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Konum: " +
                                            "${order.deliveryLatitude}, " +
                                            order.deliveryLongitude,
                                style =
                                    MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (order.customerNote.isNotBlank()) {
                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Sipariş Notu",
                                    style =
                                        MaterialTheme.typography.titleLarge
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = order.customerNote,
                                    style =
                                        MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sipariş İçeriği",
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            order.items.forEachIndexed {
                                    index,
                                    item ->

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.foodName,
                                            style =
                                                MaterialTheme.typography
                                                    .titleMedium
                                        )

                                        Text(
                                            text =
                                                "${item.quantity} adet × " +
                                                        formatOrderPrice(
                                                            item.unitPrice
                                                        ),
                                            style =
                                                MaterialTheme.typography
                                                    .bodyMedium
                                        )
                                    }

                                    Text(
                                        text =
                                            formatOrderPrice(
                                                item.totalPrice
                                            ),
                                        style =
                                            MaterialTheme.typography
                                                .titleMedium
                                    )
                                }

                                if (
                                    index <
                                    order.items.lastIndex
                                ) {
                                    HorizontalDivider(
                                        modifier =
                                            Modifier.padding(
                                                vertical = 12.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                    if (orderStatus == OrderStatus.DELIVERED) {
                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        ReviewSection(
                            uiState = uiState,

                            onRetryClick =
                                onRetryClick,

                            onShowReviewFormClick =
                                onShowReviewFormClick,

                            onHideReviewFormClick =
                                onHideReviewFormClick,

                            onRatingSelected =
                                onRatingSelected,

                            onReviewCommentChange =
                                onReviewCommentChange,

                            onSubmitReviewClick =
                                onSubmitReviewClick
                        )
                    }

                    if (orderStatus == OrderStatus.PENDING) {
                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Button(
                            onClick = {
                                showCancelDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isCancelling
                        ) {
                            if (uiState.isCancelling) {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.height(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Siparişi İptal Et")
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailInformationRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun getOrderStatusColor(
    status: OrderStatus
) = when (status) {

    OrderStatus.PENDING ->
        MaterialTheme.colorScheme.tertiary

    OrderStatus.ACCEPTED,
    OrderStatus.PREPARING,
    OrderStatus.READY,
    OrderStatus.OUT_FOR_DELIVERY,
    OrderStatus.DELIVERED ->
        MaterialTheme.colorScheme.primary

    OrderStatus.REJECTED,
    OrderStatus.CANCELLED ->
        MaterialTheme.colorScheme.error

    OrderStatus.UNKNOWN ->
        MaterialTheme.colorScheme.onSurface
}



private fun translatePaymentMethod(
    paymentMethod: String
): String {
    return when (paymentMethod) {
        "CashOnDelivery" ->
            "Kapıda Nakit"

        "CardOnDelivery" ->
            "Kapıda Kart"

        else ->
            paymentMethod
    }
}

private fun formatOrderPrice(
    price: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f TL",
        price
    )
}

private fun formatOrderDate(
    value: String
): String {
    val formatter =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm",
            Locale("tr", "TR")
        )

    return runCatching {
        OffsetDateTime
            .parse(value)
            .format(formatter)
    }.recoverCatching {
        LocalDateTime
            .parse(value)
            .format(formatter)
    }.getOrElse {
        value
    }
}
@Composable
private fun ReviewSection(
    uiState: OrderDetailUiState,
    onRetryClick: () -> Unit,
    onShowReviewFormClick: () -> Unit,
    onHideReviewFormClick: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sipariş Değerlendirmesi",
                style =
                    MaterialTheme.typography
                        .titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when {
                uiState.isReviewStatusLoading -> {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.align(
                                Alignment.CenterHorizontally
                            )
                    )
                }

                !uiState.hasCheckedReview -> {
                    Text(
                        text =
                            "Değerlendirme durumu kontrol edilemedi.",
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Tekrar Kontrol Et")
                    }
                }

                uiState.existingReview != null -> {
                    ExistingReviewContent(
                        review =
                            uiState.existingReview
                    )
                }

                uiState.isReviewFormVisible -> {
                    ReviewFormContent(
                        selectedRating =
                            uiState.selectedRating,

                        comment =
                            uiState.reviewComment,

                        isSubmitting =
                            uiState.isSubmittingReview,

                        onRatingSelected =
                            onRatingSelected,

                        onCommentChange =
                            onReviewCommentChange,

                        onSubmitClick =
                            onSubmitReviewClick,

                        onCancelClick =
                            onHideReviewFormClick
                    )
                }

                else -> {
                    Text(
                        text =
                            "Siparişiniz teslim edildi. Üreticiyi ve siparişinizi değerlendirebilirsiniz."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Button(
                        onClick =
                            onShowReviewFormClick,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Siparişi Değerlendir")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExistingReviewContent(
    review: ReviewResponse
) {
    Text(
        text =
            buildRatingStars(
                review.rating
            ),

        style =
            MaterialTheme.typography
                .headlineSmall,

        color =
            MaterialTheme.colorScheme.primary
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text =
            if (review.comment.isBlank()) {
                "Yorum yazılmadı."
            } else {
                review.comment
            },

        style =
            MaterialTheme.typography
                .bodyLarge
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text =
            "Değerlendirme tarihi: " +
                    formatOrderDate(
                        review.createdAt
                    ),

        style =
            MaterialTheme.typography
                .bodySmall
    )
}

@Composable
private fun ReviewFormContent(
    selectedRating: Int,
    comment: String,
    isSubmitting: Boolean,
    onRatingSelected: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Text(
        text = "Puanınız",
        style =
            MaterialTheme.typography
                .titleMedium
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {
        (1..5).forEach { rating ->
            TextButton(
                onClick = {
                    onRatingSelected(rating)
                },
                enabled = !isSubmitting
            ) {
                Text(
                    text =
                        if (
                            rating <= selectedRating
                        ) {
                            "★"
                        } else {
                            "☆"
                        },

                    style =
                        MaterialTheme.typography
                            .headlineSmall
                )
            }
        }
    }

    Text(
        text =
            if (selectedRating == 0) {
                "Henüz puan seçilmedi."
            } else {
                "$selectedRating / 5 puan"
            },

        style =
            MaterialTheme.typography.bodySmall
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    OutlinedTextField(
        value = comment,
        onValueChange = onCommentChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Yorumunuz")
        },
        placeholder = {
            Text(
                "Sipariş ve üretici hakkındaki düşüncelerinizi yazabilirsiniz."
            )
        },
        supportingText = {
            Text("${comment.length}/1000")
        },
        minLines = 3,
        maxLines = 6,
        enabled = !isSubmitting
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Button(
        onClick = onSubmitClick,
        modifier = Modifier.fillMaxWidth(),
        enabled =
            !isSubmitting &&
                    selectedRating in 1..5
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier =
                    Modifier.height(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Değerlendirmeyi Gönder")
        }
    }

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    TextButton(
        onClick = onCancelClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isSubmitting
    ) {
        Text("Vazgeç")
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