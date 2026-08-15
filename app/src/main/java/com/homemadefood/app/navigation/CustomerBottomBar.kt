package com.homemadefood.app.ui.customer.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.homemadefood.app.navigation.AppDestination

private data class CustomerBottomBarItem(
    val route: String,
    val label: String,
    val iconText: String
)

private val customerBottomBarItems =
    listOf(
        CustomerBottomBarItem(
            route =
                AppDestination.CustomerHome.route,

            label =
                "Ana Sayfa",

            /*
             * AŞAMA 7A işlevsel navigation aşamasıdır.
             * Tasarım aşamasında gerçek vector icon'a
             * dönüştürülecek.
             */
            iconText =
                "⌂"
        ),

        CustomerBottomBarItem(
            route =
                AppDestination.CustomerExplore.route,

            label =
                "Keşfet",

            iconText =
                "⌕"
        ),

        CustomerBottomBarItem(
            route =
                AppDestination.Orders.route,

            label =
                "Siparişler",

            iconText =
                "▤"
        ),

        CustomerBottomBarItem(
            route =
                AppDestination.CustomerAccount.route,

            label =
                "Hesabım",

            iconText =
                "●"
        )
    )

@Composable
fun CustomerBottomBar(
    selectedRoute: String,
    onDestinationClick: (String) -> Unit
) {
    NavigationBar {
        customerBottomBarItems
            .forEach { item ->
                NavigationBarItem(
                    selected =
                        selectedRoute ==
                                item.route,

                    onClick = {
                        onDestinationClick(
                            item.route
                        )
                    },

                    icon = {
                        Text(
                            text =
                                item.iconText
                        )
                    },

                    label = {
                        Text(
                            text =
                                item.label
                        )
                    }
                )
            }
    }
}