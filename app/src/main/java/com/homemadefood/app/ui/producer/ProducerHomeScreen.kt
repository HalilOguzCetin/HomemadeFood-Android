package com.homemadefood.app.ui.producer

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
import androidx.compose.material3.OutlinedButton

@Composable
fun ProducerHomeScreen(
    onApplicationStatusClick: () -> Unit,
    onCustomerModeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFoodsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onReviewsClick: () -> Unit,
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
            text = "Üretici Paneli",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text =
                "Yemeklerinizi ve gelen siparişlerinizi buradan yönetebilirsiniz.",
            style =
                MaterialTheme.typography.bodyLarge
        )
        OutlinedButton(
            onClick = onCustomerModeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Müşteri Moduna Dön")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ProducerMenuCard(
            title = "Üretici Başvurum",
            description =
                "Başvuru ve onay durumunuzu görüntüleyin.",
            buttonText = "Başvuru Durumunu Gör",
            onClick = onApplicationStatusClick
        )
        ProducerMenuCard(
            title = "İşletme Profilim",
            description =
                "İşletme bilgilerinizi, günlük kapasitenizi ve sipariş alma durumunuzu yönetin.",
            buttonText = "Profili Görüntüle",
            onClick = onProfileClick
        )

        ProducerMenuCard(
            title = "Yemeklerim",
            description =
                "Yemek ekleyin, düzenleyin ve satış durumunu yönetin.",
            buttonText = "Yemeklerimi Yönet",
            onClick = onFoodsClick
        )

        ProducerMenuCard(
            title = "Gelen Siparişler",
            description =
                "Yeni siparişleri görüntüleyin ve durumlarını güncelleyin.",
            buttonText = "Siparişleri Gör",
            onClick = onOrdersClick
        )

        ProducerMenuCard(
            title = "Değerlendirmelerim",
            description =
                "Müşterilerinizin verdiği puanları ve yorumları görüntüleyin.",
            buttonText = "Değerlendirmeleri Gör",
            onClick = onReviewsClick
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
private fun ProducerMenuCard(
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