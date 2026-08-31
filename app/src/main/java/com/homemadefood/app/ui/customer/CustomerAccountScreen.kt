package com.homemadefood.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.homemadefood.app.R
import com.homemadefood.app.data.model.ProducerApplicationStatus
import com.homemadefood.app.ui.components.AppInlineMessage
import com.homemadefood.app.ui.components.AppMessageType

/*
 * Customer Account ekranı için yalnızca bu ekrana ait ölçüler.
 * İstersen daha sonra boşlukları buradan elle değiştirebilirsin.
 */
private object CustomerAccountDp {
    val ScreenHorizontal = 20.dp
    val ScreenTop = 18.dp
    val ScreenBottom = 24.dp

    val SectionGap = 18.dp
    val CardRadius = 22.dp
    val CardPadding = 18.dp

    val ProfileAvatarSize = 82.dp
    val MenuIconSize = 46.dp
    val MenuVerticalPadding = 15.dp
}

@Composable
fun CustomerAccountScreen(
    uiState: CustomerProfileUiState,
    canUseProducerMode: Boolean,
    producerVerificationStatus: String?,
    onRetryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onPhoneVerificationClick: () -> Unit,
    onProducerApplicationClick: () -> Unit,
    onProducerModeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomerHomeTheme {
        val profile = uiState.profile

        val effectiveCanUseProducerMode =
            profile?.canUseProducerMode
                ?: canUseProducerMode

        val effectiveProducerStatus =
            profile?.producerVerificationStatus
                ?: producerVerificationStatus

        val producerStatus =
            ProducerApplicationStatus.fromBackendValue(
                effectiveProducerStatus
            )

        val producerActionTitle =
            when {
                effectiveCanUseProducerMode ->
                    "Üretici Moduna Geç"

                producerStatus == ProducerApplicationStatus.PENDING ->
                    "Üretici Başvurum"

                producerStatus == ProducerApplicationStatus.REJECTED ->
                    "Üretici Başvurumu Güncelle"

                else ->
                    "Üretici Ol"
            }

        val producerBadgeText =
            when {
                effectiveCanUseProducerMode ->
                    "Onaylandı"

                producerStatus == ProducerApplicationStatus.PENDING ->
                    "Onay bekliyor"

                producerStatus == ProducerApplicationStatus.REJECTED ->
                    "Güncelle"

                else ->
                    "Başvuru yap"
            }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomerHomeColors.Cream)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = CustomerAccountDp.ScreenHorizontal,
                    end = CustomerAccountDp.ScreenHorizontal,
                    top = CustomerAccountDp.ScreenTop,
                    bottom = CustomerAccountDp.ScreenBottom
                )
        ) {
            Text(
                text = "Hesabım",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CustomerHomeColors.DeepOlive,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (uiState.isLoading && profile == null) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = CustomerHomeColors.DeepOlive,
                    trackColor = CustomerHomeColors.OliveSoft
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (
                uiState.errorMessage != null &&
                profile == null
            ) {
                AppInlineMessage(
                    message = uiState.errorMessage,
                    type = AppMessageType.Error
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Profil bilgilerini tekrar yüklemek için dokunun.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRetryClick)
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomerHomeColors.DeepOlive,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )
            }

            AccountProfileCard(
                fullName =
                    profile?.fullName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Müşteri Hesabı",
                email =
                    profile?.email
                        ?.takeIf { it.isNotBlank() }
                        ?: "Profil bilgilerinizi görüntüleyin",
                onClick = onProfileClick
            )

            Spacer(
                modifier = Modifier.height(CustomerAccountDp.SectionGap)
            )

            AccountMenuCard {
                AccountMenuRow(
                    iconText = "○",
                    title = "Profilim",
                    onClick = onProfileClick
                )

                AccountDivider()

                AccountMenuRow(
                    iconText = "⌖",
                    title = "Adreslerim",
                    onClick = onAddressesClick
                )

                AccountDivider()

                AccountMenuRow(
                    iconText = "♡",
                    title = "Favorilerim",
                    onClick = onFavoritesClick
                )

                AccountDivider()

                AccountMenuRow(
                    iconText = "☆",
                    title = "Değerlendirmelerim",
                    onClick = onReviewsClick
                )



                AccountDivider()

                AccountMenuRow(
                    iconText = "⌂",
                    title = producerActionTitle,
                    badgeText = producerBadgeText,
                    badgeType =
                        when {
                            effectiveCanUseProducerMode ->
                                AccountBadgeType.Success

                            producerStatus == ProducerApplicationStatus.PENDING ->
                                AccountBadgeType.Neutral

                            producerStatus == ProducerApplicationStatus.REJECTED ->
                                AccountBadgeType.Warning

                            else ->
                                AccountBadgeType.Warning
                        },
                    onClick = {
                        if (effectiveCanUseProducerMode) {
                            onProducerModeClick()
                        } else {
                            onProducerApplicationClick()
                        }
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(CustomerAccountDp.SectionGap)
            )

            AccountStatusCard(
                phone = profile?.phone.orEmpty(),
                isPhoneVerified =
                    profile?.isPhoneVerified == true,
                email = profile?.email.orEmpty(),
                isEmailVerified =
                    profile?.isEmailVerified == true,
                onPhoneClick = onPhoneVerificationClick
            )

            Spacer(
                modifier = Modifier.height(CustomerAccountDp.SectionGap)
            )

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomerHomeColors.Terracotta,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Çıkış Yap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AccountProfileCard(
    fullName: String,
    email: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(CustomerAccountDp.CardRadius),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(CustomerAccountDp.CardRadius))
            .clickable(onClick = onClick),
        color = CustomerHomeColors.Surface,
        shape = RoundedCornerShape(CustomerAccountDp.CardRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CustomerAccountDp.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(CustomerAccountDp.ProfileAvatarSize)
                    .clip(CircleShape)
                    .background(CustomerHomeColors.SurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_customer_nav_account
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = CustomerHomeColors.DeepOlive
                )
            }

            Spacer(
                modifier = Modifier.size(18.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fullName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CustomerHomeColors.DeepOlive
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = email,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomerHomeColors.TextMuted
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Surface(
                    color = CustomerHomeColors.SurfaceSoft,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Müşteri Hesabı",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = CustomerHomeColors.DeepOlive,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall,
                color = CustomerHomeColors.Olive
            )
        }
    }
}

@Composable
private fun AccountMenuCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(CustomerAccountDp.CardRadius),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        color = CustomerHomeColors.Surface,
        shape = RoundedCornerShape(CustomerAccountDp.CardRadius)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
        ) {
            content()
        }
    }
}

@Composable
private fun AccountMenuRow(
    iconText: String,
    title: String,
    onClick: () -> Unit,
    badgeText: String? = null,
    badgeType: AccountBadgeType = AccountBadgeType.Neutral
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = CustomerAccountDp.MenuVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIconBubble(
            text = iconText
        )

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = CustomerHomeColors.Text,
            fontWeight = FontWeight.Medium
        )

        if (!badgeText.isNullOrBlank()) {
            AccountBadge(
                text = badgeText,
                type = badgeType
            )

            Spacer(
                modifier = Modifier.size(9.dp)
            )
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineSmall,
            color = CustomerHomeColors.Olive
        )
    }
}

@Composable
private fun AccountIconBubble(
    text: String
) {
    Box(
        modifier = Modifier
            .size(CustomerAccountDp.MenuIconSize)
            .clip(RoundedCornerShape(15.dp))
            .background(CustomerHomeColors.SurfaceSoft),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = CustomerHomeColors.DeepOlive,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AccountDivider() {
    HorizontalDivider(
        color = CustomerHomeColors.Outline.copy(alpha = 0.72f),
        thickness = 0.8.dp
    )
}

private enum class AccountBadgeType {
    Success,
    Warning,
    Neutral
}

@Composable
private fun AccountBadge(
    text: String,
    type: AccountBadgeType
) {
    val backgroundColor =
        when (type) {
            AccountBadgeType.Success ->
                CustomerHomeColors.OliveSoft

            AccountBadgeType.Warning ->
                CustomerHomeColors.TerracottaSoft

            AccountBadgeType.Neutral ->
                CustomerHomeColors.SurfaceSoft
        }

    val contentColor =
        when (type) {
            AccountBadgeType.Success ->
                CustomerHomeColors.DeepOlive

            AccountBadgeType.Warning ->
                CustomerHomeColors.Terracotta

            AccountBadgeType.Neutral ->
                CustomerHomeColors.TextMuted
        }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AccountStatusCard(
    phone: String,
    isPhoneVerified: Boolean,
    email: String,
    isEmailVerified: Boolean,
    onPhoneClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(CustomerAccountDp.CardRadius),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        color = CustomerHomeColors.Surface,
        shape = RoundedCornerShape(CustomerAccountDp.CardRadius)
    ) {
        Column(
            modifier = Modifier.padding(CustomerAccountDp.CardPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccountIconBubble(
                    text = "✓"
                )

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = "Hesap Durumu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CustomerHomeColors.DeepOlive
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            AccountStatusRow(
                iconText = "☎",
                title = "Telefon",
                value =
                    phone.takeIf { it.isNotBlank() }
                        ?: "Telefon numarası eklenmedi",
                verified = isPhoneVerified,
                onClick = onPhoneClick
            )

            AccountDivider()

            AccountStatusRow(
                iconText = "✉",
                title = "E-posta",
                value =
                    email.takeIf { it.isNotBlank() }
                        ?: "E-posta bilgisi alınamadı",
                verified = isEmailVerified,
                onClick = null
            )
        }
    }
}

@Composable
private fun AccountStatusRow(
    iconText: String,
    title: String,
    value: String,
    verified: Boolean,
    onClick: (() -> Unit)?
) {
    val rowModifier =
        if (onClick != null) {
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 13.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp)
        }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIconBubble(
            text = iconText
        )

        Spacer(
            modifier = Modifier.size(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = CustomerHomeColors.Text,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomerHomeColors.TextMuted
            )
        }

        AccountBadge(
            text =
                if (verified) {
                    "Doğrulandı"
                } else {
                    "Doğrulanmadı"
                },
            type =
                if (verified) {
                    AccountBadgeType.Success
                } else {
                    AccountBadgeType.Warning
                }
        )
    }
}