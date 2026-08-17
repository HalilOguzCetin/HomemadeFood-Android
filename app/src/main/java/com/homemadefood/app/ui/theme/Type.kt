package com.homemadefood.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
 * Harici font dosyasına bağımlı olmadan daha güçlü
 * ve modern bir HomemadeFood tipografi hiyerarşisi.
 */
private val HomemadeFontFamily =
    FontFamily.SansSerif

val Typography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.5).sp
            ),

        headlineMedium =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.3).sp
            ),

        headlineSmall =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            ),

        titleLarge =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp
            ),

        titleMedium =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),

        titleSmall =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),

        bodyLarge =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),

        bodyMedium =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),

        bodySmall =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 17.sp
            ),

        labelLarge =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),

        labelMedium =
            TextStyle(
                fontFamily =
                    HomemadeFontFamily,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
    )