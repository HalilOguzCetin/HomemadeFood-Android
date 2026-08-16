package com.homemadefood.app.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.homemadefood.app.data.model.ProducerApplicationStatus

@Composable
fun CustomerAccountScreen(
    canUseProducerMode: Boolean,
    producerVerificationStatus: String?,
    onProfileClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onProducerApplicationClick: () -> Unit,
    onProducerModeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                )
    ) {
        Text(
            text = "Hesabım",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Kişisel işlemlerinizi ve hesap ayarlarınızı buradan yönetebilirsiniz.",

            modifier =
                Modifier.padding(
                    top = 6.dp
                ),

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        AccountSectionTitle(
            text = "Kişisel İşlemler"
        )

        AccountActionRow(
            title = "Profil Bilgilerim",
            description =
                "Ad, e-posta ve telefon bilgilerinizi görüntüleyin",

            onClick =
                onProfileClick
        )

        AccountActionRow(
            title = "Adreslerim",
            description =
                "Kayıtlı teslimat adreslerini yönet",

            onClick =
                onAddressesClick
        )

        AccountActionRow(
            title = "Favorilerim",
            description =
                "Favoriye eklediğiniz yemekleri görüntüle",

            onClick =
                onFavoritesClick
        )

        AccountActionRow(
            title = "Değerlendirmelerim",
            description =
                "Siparişleriniz için yaptığınız değerlendirmeleri görüntüle",

            onClick =
                onReviewsClick
        )

        Spacer(
            modifier =
                Modifier.height(22.dp)
        )

        AccountSectionTitle(
            text = "Üretici İşlemleri"
        )

        val producerStatus =
            ProducerApplicationStatus
                .fromBackendValue(
                    producerVerificationStatus
                )

        val producerActionTitle =
            when {
                canUseProducerMode ->
                    "Üretici Moduna Geç"

                producerStatus ==
                        ProducerApplicationStatus.PENDING ->
                    "Üretici Başvurum"

                producerStatus ==
                        ProducerApplicationStatus.REJECTED ->
                    "Üretici Başvurumu Güncelle"

                else ->
                    "Üretici Ol"
            }

        val producerActionDescription =
            when {
                canUseProducerMode ->
                    "Onaylı işletmenizin üretici panelini aç"

                producerStatus ==
                        ProducerApplicationStatus.PENDING ->
                    "Başvurunuzun mevcut durumunu görüntüle"

                producerStatus ==
                        ProducerApplicationStatus.REJECTED ->
                    "Başvurunuzu inceleyip yeniden düzenle"

                else ->
                    "HomemadeFood üzerinde işletme başvurusu oluştur"
            }

        AccountActionRow(
            title =
                producerActionTitle,

            description =
                producerActionDescription,

            onClick = {
                if (canUseProducerMode) {
                    onProducerModeClick()
                } else {
                    onProducerApplicationClick()
                }
            }
        )

        Spacer(
            modifier =
                Modifier.height(22.dp)
        )

        AccountSectionTitle(
            text = "Hesap"
        )

        TextButton(
            onClick =
                onLogoutClick,

            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Çıkış Yap",

                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }
}

@Composable
private fun AccountSectionTitle(
    text: String
) {
    Text(
        text = text,

        modifier =
            Modifier.padding(
                bottom = 8.dp
            ),

        style =
            MaterialTheme
                .typography
                .titleMedium,

        fontWeight =
            FontWeight.SemiBold
    )
}

@Composable
private fun AccountActionRow(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    vertical = 14.dp
                )
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = title,

                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,

                    fontWeight =
                        FontWeight.Medium
                )

                Text(
                    text = "›",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge
                )
            }

            Text(
                text = description,

                modifier =
                    Modifier.padding(
                        top = 4.dp,
                        end = 28.dp
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }

    HorizontalDivider()
}