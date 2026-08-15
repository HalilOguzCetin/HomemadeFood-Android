package com.homemadefood.app.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CustomerCartButton(
    totalQuantity: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (totalQuantity > 0) {
                Badge {
                    Text(
                        text =
                            if (totalQuantity > 99) {
                                "99+"
                            } else {
                                totalQuantity.toString()
                            }
                    )
                }
            }
        }
    ) {
        IconButton(
            onClick = onClick
        ) {
            /*
             * AŞAMA 7D.1 işlevsel sepet erişimi + badge aşamasıdır.
             * Görsel tasarım aşamasında bu geçici sembol,
             * uygulamanın gerçek vector sepet ikonuna çevrilecek.
             */
            Text(
                text = "🛒",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}