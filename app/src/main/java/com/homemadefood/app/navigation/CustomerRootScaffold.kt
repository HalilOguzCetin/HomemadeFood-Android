package com.homemadefood.app.ui.customer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.homemadefood.app.ui.customer.CustomerHomeColors

@Composable
fun CustomerRootScaffold(
    selectedRoute: String,
    onBottomDestinationClick: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor =
            CustomerHomeColors
                .Cream,

        bottomBar = {
            CustomerBottomBar(
                selectedRoute =
                    selectedRoute,

                onDestinationClick =
                    onBottomDestinationClick
            )
        }
    ) { innerPadding ->
        content(
            innerPadding
        )
    }
}