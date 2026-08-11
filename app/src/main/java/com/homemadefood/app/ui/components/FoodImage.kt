package com.homemadefood.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.homemadefood.app.data.remote.ApiConfig

@Composable
fun FoodImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val resolvedUrl =
        ApiConfig.resolveMediaUrl(
            imageUrl
        )

    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme
                    .surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        if (resolvedUrl == null) {
            Text(
                text = "Görsel yok",
                style =
                    MaterialTheme.typography
                        .labelMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            AsyncImage(
                model = resolvedUrl,
                contentDescription =
                    contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        }
    }
}