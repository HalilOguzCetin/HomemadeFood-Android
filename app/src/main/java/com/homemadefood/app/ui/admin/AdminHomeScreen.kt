package com.homemadefood.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminHomeScreen(
    onProducerApplicationsClick: () -> Unit,
    onRecommendationAnalyticsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Admin Paneli",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Üretici başvurularını ve sistem analizlerini buradan yönetebilirsiniz.",
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AdminMenuCard(
            title = "Üretici Başvuruları",

            description = "Bekleyen üretici başvurularını inceleyin, onaylayın veya gerekçesiyle reddedin.",

            buttonText = "Başvuruları Gör",

            onClick =
                onProducerApplicationsClick
        )

        AdminMenuCard(
            title = "Öneri Sistemi Analizi",

            description = "Akıllı üretici seçimi ve sipariş yönlendirme sonuçlarını inceleyin.",

            buttonText = "Analizleri Gör",

            onClick =
                onRecommendationAnalyticsClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Çıkış Yap")
        }
    }
}

@Composable
private fun AdminMenuCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
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
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = description,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}