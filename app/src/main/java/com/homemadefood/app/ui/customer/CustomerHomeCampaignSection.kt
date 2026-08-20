package com.homemadefood.app.ui.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private data class CustomerHomeCampaign(
    val eyebrow: String,
    val title: String,
    val description: String,
    val emoji: String,
    val startColor: Color,
    val endColor: Color,
    val lightContent: Boolean
)

private val homeCampaigns =
    listOf(
        CustomerHomeCampaign(
            eyebrow = "HAFTA SONUNA ÖZEL",
            title = "Ev yapımı sofralara küçük bir sürpriz",
            description =
                "Seçili işletmelerde hafta sonu fırsatlarını keşfedin.",
            emoji = "🍲",
            startColor =
                CustomerHomeColors.DeepOlive,
            endColor =
                Color(0xFF506B43),
            lightContent = true
        ),

        CustomerHomeCampaign(
            eyebrow = "YENİ MUTFAKLAR",
            title = "Yeni işletmeler aramıza katıldı",
            description =
                "Mahallenizdeki yeni ev mutfaklarına göz atın.",
            emoji = "🥘",
            startColor =
                CustomerHomeColors.Terracotta,
            endColor =
                Color(0xFFE19A71),
            lightContent = true
        ),

        CustomerHomeCampaign(
            eyebrow = "TATLI MOLASI",
            title = "Günün tatlısını keşfedin",
            description =
                "Ev yapımı tatlılarla gününüze sıcak bir mola ekleyin.",
            emoji = "🍰",
            startColor =
                Color(0xFFFFE5C7),
            endColor =
                Color(0xFFFFF4E7),
            lightContent = false
        )
    )

@Composable
fun CustomerHomeCampaignSection(
    modifier: Modifier = Modifier
) {
    val listState =
        rememberLazyListState()

    val activeIndex by
    remember {
        derivedStateOf {
            val layoutInfo =
                listState.layoutInfo

            val visibleItems =
                layoutInfo.visibleItemsInfo

            if (visibleItems.isEmpty()) {
                0
            } else {
                val viewportCenter =
                    (
                            layoutInfo
                                .viewportStartOffset +
                                    layoutInfo
                                        .viewportEndOffset
                            ) / 2

                visibleItems
                    .minByOrNull { itemInfo ->
                        val itemCenter =
                            itemInfo.offset +
                                    (
                                            itemInfo.size /
                                                    2
                                            )

                        abs(
                            itemCenter -
                                    viewportCenter
                        )
                    }
                    ?.index
                    ?.coerceIn(
                        0,
                        homeCampaigns
                            .lastIndex
                    )
                    ?: 0
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {
            itemsIndexed(
                items = homeCampaigns,
                key = {
                        index,
                        campaign ->
                    "${index}_${campaign.title}"
                }
            ) {
                    _,
                    campaign ->

                CustomerCampaignCard(
                    campaign = campaign
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.Center,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            homeCampaigns.forEachIndexed {
                    index,
                    _ ->

                val selected =
                    index == activeIndex

                Box(
                    modifier =
                        Modifier
                            .padding(
                                horizontal = 3.dp
                            )
                            .size(
                                if (selected) {
                                    8.dp
                                } else {
                                    6.dp
                                }
                            )
                            .background(
                                color =
                                    if (selected) {
                                        CustomerHomeColors
                                            .DeepOlive
                                    } else {
                                        CustomerHomeColors
                                            .Outline
                                    },
                                shape = CircleShape
                            )
                )
            }
        }
    }
}

@Composable
private fun CustomerCampaignCard(
    campaign: CustomerHomeCampaign
) {
    val contentColor =
        if (campaign.lightContent) {
            Color.White
        } else {
            CustomerHomeColors.DeepOlive
        }

    Card(
        modifier =
            Modifier
                .width(286.dp)
                .height(152.dp),
        shape =
            RoundedCornerShape(
                24.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(152.dp)
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        campaign.startColor,
                                        campaign.endColor
                                    )
                            )
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    )
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 76.dp
                        )
            ) {
                Text(
                    text = campaign.eyebrow,
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        contentColor
                            .copy(alpha = 0.78f)
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text = campaign.title,
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.Bold,
                    color = contentColor,
                    maxLines = 2
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        campaign.description,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        contentColor
                            .copy(alpha = 0.88f),
                    maxLines = 2
                )
            }

            Surface(
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .size(62.dp),
                shape = CircleShape,
                color =
                    if (campaign.lightContent) {
                        Color.White
                            .copy(alpha = 0.15f)
                    } else {
                        CustomerHomeColors
                            .DeepOlive
                            .copy(alpha = 0.08f)
                    }
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = campaign.emoji,
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )
                }
            }
        }
    }
}