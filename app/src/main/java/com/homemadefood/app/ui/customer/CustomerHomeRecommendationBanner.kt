package com.homemadefood.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CustomerHomeRecommendationBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        22.dp
                    )
                )
                .clickable(
                    onClick = onClick
                ),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        color =
            CustomerHomeColors
                .OliveSoft,
        tonalElevation =
            1.dp
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RecommendationIllustration()

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {
                Text(
                    text =
                        "Ne yesem diye düşünüyorsan...",
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text =
                        "Akıllı Üretici Önerisi",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        CustomerHomeColors
                            .DeepOlive,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text =
                        "Sana uygun lezzetleri keşfet",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        CustomerHomeColors
                            .TextMuted,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )

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
                    CustomerHomeColors
                        .DeepOlive
            ) {
                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            5.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = "✦",
                        color =
                            CustomerHomeColors
                                .Gold,
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Önerileri Gör",
                        color =
                            CustomerHomeColors
                                .Cream,
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationIllustration() {
    Box(
        modifier =
            Modifier.size(
                58.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            modifier =
                Modifier.size(
                    52.dp
                ),
            shape =
                CircleShape,
            color =
                CustomerHomeColors
                    .Surface
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "🥗",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium
                )
            }
        }

        Text(
            text = "✦",
            modifier =
                Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .background(
                        color =
                            CustomerHomeColors
                                .TerracottaSoft,
                        shape =
                            CircleShape
                    )
                    .padding(
                        horizontal =
                            4.dp,
                        vertical =
                            1.dp
                    ),
            color =
                CustomerHomeColors
                    .Terracotta,
            style =
                MaterialTheme
                    .typography
                    .labelSmall,
            fontWeight =
                FontWeight.Bold
        )
    }
}