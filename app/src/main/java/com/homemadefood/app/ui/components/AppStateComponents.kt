package com.homemadefood.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class AppMessageType {
    Success,
    Error,
    Info
}

@Composable
fun AppLoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()

        if (!message.isNullOrBlank()) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppErrorState(
    message: String,
    onRetryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    retryText: String = "Tekrar Dene"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bir sorun oluştu",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetryClick != null) {
            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onRetryClick
            ) {
                Text(retryText)
            }
        }
    }
}

@Composable
fun AppEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Henüz burada bir şey yok"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AppInlineMessage(
    message: String,
    type: AppMessageType,
    modifier: Modifier = Modifier
) {
    val containerColor =
        when (type) {
            AppMessageType.Success ->
                MaterialTheme.colorScheme.primaryContainer

            AppMessageType.Error ->
                MaterialTheme.colorScheme.errorContainer

            AppMessageType.Info ->
                MaterialTheme.colorScheme.secondaryContainer
        }

    val contentColor =
        when (type) {
            AppMessageType.Success ->
                MaterialTheme.colorScheme.onPrimaryContainer

            AppMessageType.Error ->
                MaterialTheme.colorScheme.onErrorContainer

            AppMessageType.Info ->
                MaterialTheme.colorScheme.onSecondaryContainer
        }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}