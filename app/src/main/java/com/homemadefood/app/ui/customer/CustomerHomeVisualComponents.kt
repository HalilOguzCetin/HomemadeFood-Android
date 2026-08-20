package com.homemadefood.app.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.homemadefood.app.R

/*
 * Customer Home için görsel tokenlar.
 *
 * Login/Auth ekranında oturan sıcak zeytin + terracotta + krem
 * dilini Customer tarafına taşır. Home bölümleri ilerledikçe
 * aynı renkler tek noktadan kullanılacak.
 */
internal object CustomerHomeColors {
    val DeepOlive = Color(0xFF23462E)
    val Olive = Color(0xFF4D6444)
    val OliveSoft = Color(0xFFE6EBD8)

    val Terracotta = Color(0xFFCB6F4D)
    val TerracottaSoft = Color(0xFFF7DFD4)

    val Cream = Color(0xFFFFFBF4)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceSoft = Color(0xFFFFF7ED)

    val Text = Color(0xFF1E2D22)
    val TextMuted = Color(0xFF6D756D)
    val Outline = Color(0xFFE2DDD4)

    val Gold = Color(0xFFE7A83B)
    val Error = Color(0xFFBA1A1A)
}

/*
 * Home'u cihazın dark-mode ayarından bağımsız olarak
 * onaylanan açık/sıcak tasarım dilinde gösterir.
 *
 * Bottom navigation H8'de ayrıca tasarlanacak.
 */
private val customerHomeColorScheme =
    lightColorScheme(
        primary = CustomerHomeColors.DeepOlive,
        onPrimary = Color.White,
        primaryContainer =
            CustomerHomeColors.OliveSoft,
        onPrimaryContainer =
            CustomerHomeColors.DeepOlive,

        secondary = CustomerHomeColors.Terracotta,
        onSecondary = Color.White,
        secondaryContainer =
            CustomerHomeColors.TerracottaSoft,
        onSecondaryContainer =
            CustomerHomeColors.Text,

        background = CustomerHomeColors.Cream,
        onBackground = CustomerHomeColors.Text,

        surface = CustomerHomeColors.Surface,
        onSurface = CustomerHomeColors.Text,

        surfaceVariant =
            CustomerHomeColors.SurfaceSoft,
        onSurfaceVariant =
            CustomerHomeColors.TextMuted,

        outline = CustomerHomeColors.Outline,

        error = CustomerHomeColors.Error,
        onError = Color.White
    )

@Composable
internal fun CustomerHomeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme =
            customerHomeColorScheme,
        typography =
            MaterialTheme.typography,
        content = content
    )
}

@Composable
internal fun CustomerHomeCartButton(
    totalQuantity: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (totalQuantity > 0) {
                Badge(
                    containerColor =
                        CustomerHomeColors
                            .Terracotta,
                    contentColor =
                        Color.White
                ) {
                    Text(
                        text =
                            if (totalQuantity > 99) {
                                "99+"
                            } else {
                                totalQuantity
                                    .toString()
                            }
                    )
                }
            }
        }
    ) {
        Surface(
            modifier =
                Modifier
                    .size(48.dp)
                    .clickable(
                        onClick = onClick
                    ),
            shape = CircleShape,
            color =
                CustomerHomeColors
                    .DeepOlive,
            shadowElevation = 5.dp,
            border =
                BorderStroke(
                    width = 1.dp,
                    color =
                        CustomerHomeColors
                            .DeepOlive
                            .copy(alpha = 0.08f)
                )
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_customer_home_cart
                        ),
                    contentDescription =
                        "Sepet",
                    tint = Color.White,
                    modifier =
                        Modifier.size(24.dp)
                )
            }
        }
    }
}