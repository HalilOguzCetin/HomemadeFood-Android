package com.homemadefood.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * HomemadeFood kendi marka paletini kullanır.
 * Dynamic Color bilinçli olarak kaldırılmıştır:
 * uygulamanın ana rengi cihaz duvar kağıdına göre değişmemelidir.
 */

private val DarkColorScheme =
    darkColorScheme(
        primary = HomemadeAmberDarkTheme,
        onPrimary = HomemadeText,
        primaryContainer =
            HomemadeAmberContainerDark,
        onPrimaryContainer =
            HomemadeOnAmberContainerDark,

        secondary = HomemadeBrownDarkTheme,
        onSecondary = HomemadeText,
        secondaryContainer =
            HomemadeBrownContainerDark,
        onSecondaryContainer =
            HomemadeOnBrownContainerDark,

        background = HomemadeBackgroundDark,
        onBackground = HomemadeTextDark,

        surface = HomemadeSurfaceDark,
        onSurface = HomemadeTextDark,
        surfaceVariant =
            HomemadeSurfaceVariantDark,
        onSurfaceVariant =
            HomemadeTextMutedDark,

        outline = HomemadeOutlineDark,

        error = HomemadeError,
        onError = HomemadeOnError
    )

private val LightColorScheme =
    lightColorScheme(
        primary = HomemadeAmber,
        onPrimary = HomemadeText,
        primaryContainer =
            HomemadeAmberContainer,
        onPrimaryContainer =
            HomemadeOnAmberContainer,

        secondary = HomemadeBrown,
        onSecondary = HomemadeOnError,
        secondaryContainer =
            HomemadeBrownContainer,
        onSecondaryContainer =
            HomemadeOnBrownContainer,

        background = HomemadeBackground,
        onBackground = HomemadeText,

        surface = HomemadeSurface,
        onSurface = HomemadeText,
        surfaceVariant =
            HomemadeSurfaceVariant,
        onSurfaceVariant =
            HomemadeTextMuted,

        outline = HomemadeOutline,

        error = HomemadeError,
        onError = HomemadeOnError
    )

@Composable
fun HomemadeFoodTheme(
    darkTheme: Boolean =
        isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme =
            if (darkTheme) {
                DarkColorScheme
            } else {
                LightColorScheme
            },
        typography = Typography,
        content = content
    )
}