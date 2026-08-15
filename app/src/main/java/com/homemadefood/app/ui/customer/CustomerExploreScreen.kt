package com.homemadefood.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomerExploreScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Keşfet",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Text(
            text =
                "İşletme arama ve gelişmiş kategori keşfi sonraki alt aşamada bu ekrana taşınacak.",

            modifier =
                Modifier.padding(
                    top = 12.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}