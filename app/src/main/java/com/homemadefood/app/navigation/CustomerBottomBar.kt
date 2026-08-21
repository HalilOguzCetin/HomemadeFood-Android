package com.homemadefood.app.ui.customer.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.homemadefood.app.R
import com.homemadefood.app.navigation.AppDestination
import com.homemadefood.app.ui.customer.CustomerHomeColors

private data class CustomerBottomBarItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

private val customerBottomBarItems =
    listOf(
        CustomerBottomBarItem(
            route =
                AppDestination
                    .CustomerHome
                    .route,
            label =
                "Ana Sayfa",
            iconRes =
                R.drawable
                    .ic_customer_nav_home
        ),

        CustomerBottomBarItem(
            route =
                AppDestination
                    .CustomerExplore
                    .route,
            label =
                "Keşfet",
            iconRes =
                R.drawable
                    .ic_customer_nav_explore
        ),

        CustomerBottomBarItem(
            route =
                AppDestination
                    .Orders
                    .route,
            label =
                "Siparişler",
            iconRes =
                R.drawable
                    .ic_customer_nav_orders
        ),

        CustomerBottomBarItem(
            route =
                AppDestination
                    .CustomerAccount
                    .route,
            label =
                "Hesabım",
            iconRes =
                R.drawable
                    .ic_customer_nav_account
        )
    )

@Composable
fun CustomerBottomBar(
    selectedRoute: String,
    onDestinationClick: (String) -> Unit
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 4.dp
                ),
        color =
            CustomerHomeColors
                .Surface,
        shadowElevation =
            5.dp
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    /*
                     * Material NavigationBar'ın varsayılan
                     * yüksekliğinden daha kompakt.
                     */
                    .height(
                        58.dp
                    )
                    .padding(
                        horizontal =
                            8.dp,
                        vertical =
                            4.dp
                    ),
            horizontalArrangement =
                Arrangement.SpaceEvenly,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            customerBottomBarItems
                .forEach { item ->

                    val selected =
                        selectedRoute ==
                                item.route

                    CustomerBottomBarItemView(
                        item =
                            item,
                        selected =
                            selected,
                        onClick = {
                            if (!selected) {
                                onDestinationClick(
                                    item.route
                                )
                            }
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )
                }
        }
    }
}

@Composable
private fun CustomerBottomBarItemView(
    item: CustomerBottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .height(
                    50.dp
                )
                .clickable(
                    onClick =
                        onClick
                ),
        contentAlignment =
            Alignment.Center
    ) {
        Surface(
            shape =
                RoundedCornerShape(
                    18.dp
                ),
            color =
                if (selected) {
                    CustomerHomeColors
                        .OliveSoft
                } else {
                    CustomerHomeColors
                        .Surface
                }
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(
                            horizontal =
                                13.dp,
                            vertical =
                                5.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(
                        1.dp
                    )
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                item.iconRes
                        ),
                    contentDescription =
                        item.label,
                    modifier =
                        Modifier.size(
                            21.dp
                        ),
                    tint =
                        if (selected) {
                            CustomerHomeColors
                                .DeepOlive
                        } else {
                            CustomerHomeColors
                                .TextMuted
                        }
                )

                Text(
                    text =
                        item.label,
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
                    fontWeight =
                        if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                    color =
                        if (selected) {
                            CustomerHomeColors
                                .DeepOlive
                        } else {
                            CustomerHomeColors
                                .TextMuted
                        },
                    maxLines =
                        1
                )
            }
        }
    }
}