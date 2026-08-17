package com.homemadefood.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Login ekranı HomemadeFood'un auth tasarım dili için
 * referans bileşenidir.
 *
 * İleride gerçek logo hazır olduğunda yalnızca
 * AuthBrandLogo içeriği değiştirilebilir.
 */
internal object AuthVisualColors {
    val DeepOlive = Color(0xFF24452E)
    val OliveText = Color(0xFF1E3B26)
    val BrandOlive = Color(0xFF4F5F3C)

    val Gold = Color(0xFFC8A86D)
    val Cream = Color(0xFFFFF7E9)

    val GlassCard = Color(0xFFFFFCF9)
    val FieldSurface = Color(0xFFFFFFFF)
    val FieldBorder = Color(0xFFE5CFC4)
    val FieldFocused = Color(0xFFC8866F)

    val Terracotta = Color(0xFFC96F4F)
    val TerracottaPressed = Color(0xFFB65E41)

    val InfoSurface = Color(0xFFDC9278)
    val InfoContent = Color(0xFFFFFAF5)
}

@Composable
internal fun AuthBrandLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(94.dp)
            .shadow(
                elevation = 7.dp,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = AuthVisualColors.DeepOlive,
                shape = CircleShape
            )
            .border(
                width = 4.dp,
                color = AuthVisualColors.Gold,
                shape = CircleShape
            )
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = AuthVisualColors.Gold,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "HF",
            color = AuthVisualColors.Cream,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 31.sp
        )
    }
}

@Composable
internal fun AuthGlassCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = 24.dp,
            vertical = 28.dp
        ),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        color =
            AuthVisualColors.GlassCard
                .copy(alpha = 0.88f),
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    Color.White.copy(
                        alpha = 0.62f
                    )
            ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        androidx.compose.foundation.layout.Column(
            modifier =
                Modifier.padding(
                    contentPadding
                ),
            content = content
        )
    }
}

@Composable
internal fun authTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor =
            AuthVisualColors.OliveText,
        unfocusedTextColor =
            AuthVisualColors.OliveText,

        focusedLabelColor =
            AuthVisualColors.DeepOlive,
        unfocusedLabelColor =
            AuthVisualColors.BrandOlive,

        focusedPlaceholderColor =
            AuthVisualColors.BrandOlive
                .copy(alpha = 0.62f),
        unfocusedPlaceholderColor =
            AuthVisualColors.BrandOlive
                .copy(alpha = 0.52f),

        cursorColor =
            AuthVisualColors.DeepOlive,

        focusedBorderColor =
            AuthVisualColors.FieldFocused,
        unfocusedBorderColor =
            AuthVisualColors.FieldBorder,

        focusedContainerColor =
            AuthVisualColors.FieldSurface
                .copy(alpha = 0.30f),
        unfocusedContainerColor =
            AuthVisualColors.FieldSurface
                .copy(alpha = 0.22f),

        disabledContainerColor =
            AuthVisualColors.FieldSurface
                .copy(alpha = 0.14f),

        errorBorderColor =
            MaterialTheme.colorScheme.error,
        errorLabelColor =
            MaterialTheme.colorScheme.error,
        errorCursorColor =
            MaterialTheme.colorScheme.error
    )

@Composable
internal fun authPrimaryButtonColors() =
    ButtonDefaults.buttonColors(
        containerColor =
            AuthVisualColors.Terracotta,
        contentColor =
            Color.White,
        disabledContainerColor =
            AuthVisualColors.Terracotta
                .copy(alpha = 0.48f),
        disabledContentColor =
            Color.White.copy(
                alpha = 0.76f
            )
    )

@Composable
internal fun AuthMessageCard(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isError) {
            MaterialTheme
                .colorScheme
                .errorContainer
                .copy(alpha = 0.92f)
        } else {
            AuthVisualColors.InfoSurface
                .copy(alpha = 0.94f)
        }

    val contentColor =
        if (isError) {
            MaterialTheme
                .colorScheme
                .onErrorContainer
        } else {
            AuthVisualColors.InfoContent
        }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp
    ) {
        androidx.compose.foundation.layout.Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "ⓘ",
                color = contentColor,
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Medium
            )

            Text(
                text = message,
                modifier =
                    Modifier.padding(
                        start = 12.dp
                    ),
                color = contentColor,
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge
            )
        }
    }
}