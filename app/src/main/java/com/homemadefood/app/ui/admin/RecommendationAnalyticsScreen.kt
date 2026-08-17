package com.homemadefood.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.RecommendationPerformanceResponse
import com.homemadefood.app.ui.components.AppEmptyState
import com.homemadefood.app.ui.components.AppErrorState
import com.homemadefood.app.ui.components.AppLoadingState
import java.util.Locale

@Composable
fun RecommendationAnalyticsScreen(
    uiState: RecommendationAnalyticsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        item {
            TextButton(
                onClick = onBackClick
            ) {
                Text("← Admin Paneline Dön")
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Öneri Sistemi Analizi",
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Yapay zekâ destekli üretici seçimi ve sipariş yönlendirme sonuçları.",

                style =
                    MaterialTheme.typography.bodyMedium
            )
        }

        when {
            uiState.isLoading -> {
                item {
                    AppLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        message = "Analiz bilgileri yükleniyor..."
                    )
                }
            }

            uiState.errorMessage != null -> {
                item {
                    AppErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }

            uiState.analytics != null -> {
                val analytics =
                    uiState.analytics

                item {
                    AnalyticsSummaryCard(
                        analytics = analytics
                    )
                }

                item {
                    AnalyticsGroupCard(
                        title =
                            "Arama ve Öneri Sonuçları",

                        values = listOf(
                            "Toplam öneri araması" to
                                    analytics.totalSearches
                                        .toString(),

                            "Öneri bulunan arama" to
                                    analytics
                                        .searchesWithRecommendations
                                        .toString(),

                            "Öneri bulunamayan arama" to
                                    analytics
                                        .searchesWithoutRecommendations
                                        .toString(),

                            "Gösterilen toplam aday" to
                                    analytics
                                        .totalCandidatesShown
                                        .toString(),

                            "Seçim yapılan arama" to
                                    analytics
                                        .selectedSearches
                                        .toString()
                        )
                    )
                }

                item {
                    AnalyticsGroupCard(
                        title = "Sipariş Sonuçları",

                        values = listOf(
                            "Öneriden oluşturulan sipariş" to
                                    analytics
                                        .recommendationOrders
                                        .toString(),

                            "Teslim edilen sipariş" to
                                    analytics
                                        .deliveredOrders
                                        .toString(),

                            "İptal edilen sipariş" to
                                    analytics
                                        .cancelledOrders
                                        .toString(),

                            "Üretici tarafından reddedilen" to
                                    analytics
                                        .rejectedOrders
                                        .toString(),

                            "Değerlendirilen sipariş" to
                                    analytics
                                        .reviewedOrders
                                        .toString()
                        )
                    )
                }

                item {
                    AnalyticsGroupCard(
                        title = "Başarı ve Dönüşüm Oranları",

                        values = listOf(
                            "Aramadan seçime dönüşüm" to
                                    formatPercentage(
                                        analytics
                                            .searchToSelectionRate
                                    ),

                            "Seçimden siparişe dönüşüm" to
                                    formatPercentage(
                                        analytics
                                            .selectionToOrderRate
                                    ),

                            "Sipariş teslim oranı" to
                                    formatPercentage(
                                        analytics
                                            .orderDeliveryRate
                                    ),

                            "Değerlendirme oranı" to
                                    formatPercentage(
                                        analytics.reviewRate
                                    )
                        )
                    )
                }

                item {
                    AnalyticsGroupCard(
                        title = "Ortalama Değerler",

                        values = listOf(
                            "Ortalama uygunluk puanı" to
                                    formatDecimal(
                                        analytics
                                            .averageSuitabilityScore
                                    ),

                            "Ortalama müşteri puanı" to
                                    "${
                                        formatDecimal(
                                            analytics
                                                .averageCustomerRating
                                        )
                                    } / 5",

                            "Seçilen ortalama sıra" to
                                    formatDecimal(
                                        analytics
                                            .averageSelectedRank
                                    )
                        )
                    )
                }
            }

            else -> {
                item {
                    AppEmptyState(
                        title = "Analiz bilgisi bulunamadı",
                        message =
                            "Öneri sistemi kullanıldıkça analiz sonuçları burada görüntülenecek.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
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
private fun AnalyticsSummaryCard(
    analytics: RecommendationPerformanceResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Genel Özet",
                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "${analytics.totalSearches} öneri araması gerçekleştirildi.",

                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "${analytics.recommendationOrders} sipariş öneri sistemi üzerinden oluşturuldu.",

                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Teslim oranı: ${
                        formatPercentage(
                            analytics.orderDeliveryRate
                        )
                    }",

                style =
                    MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AnalyticsGroupCard(
    title: String,
    values: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            values.forEachIndexed {
                    index,
                    information ->

                AnalyticsInformation(
                    title = information.first,
                    value = information.second
                )

                if (index < values.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsInformation(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleMedium
        )
    }
}

private fun formatPercentage(
    value: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f%%",
        value
    )
}

private fun formatDecimal(
    value: Double
): String {
    return String.format(
        Locale("tr", "TR"),
        "%.2f",
        value
    )
}