package com.homemadefood.app.ui.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class LocationPermissionLevel {
    NONE,
    APPROXIMATE,
    PRECISE
}

@Composable
fun LocationPermissionSection(
    modifier: Modifier = Modifier,
    onPermissionLevelChanged: (
        LocationPermissionLevel
    ) -> Unit = {}
) {
    val context =
        LocalContext.current

    val activity =
        remember(context) {
            context.findActivity()
        }

    var permissionLevel by
    remember {
        mutableStateOf(
            context.currentLocationPermissionLevel()
        )
    }

    /*
     * Ayarlara gidip uygulamaya dönerse güncel izni
     * tekrar okuyabilmek için request sonucu dışında da
     * state'i senkron tutuyoruz.
     */
    LaunchedEffect(Unit) {
        permissionLevel =
            context.currentLocationPermissionLevel()

        onPermissionLevelChanged(
            permissionLevel
        )
    }

    var hasRequestedPermission by
    rememberSaveable {
        mutableStateOf(false)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestMultiplePermissions()
        ) {
            hasRequestedPermission = true

            permissionLevel =
                context
                    .currentLocationPermissionLevel()

            onPermissionLevelChanged(
                permissionLevel
            )
        }

    val shouldShowRationale =
        activity?.let {
            ActivityCompat
                .shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission
                        .ACCESS_FINE_LOCATION
                ) ||
                    ActivityCompat
                        .shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission
                                .ACCESS_COARSE_LOCATION
                        )
        } ?: false

    val permissionBlocked =
        hasRequestedPermission &&
                permissionLevel ==
                LocationPermissionLevel.NONE &&
                !shouldShowRationale

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (permissionLevel) {
            LocationPermissionLevel.PRECISE -> {
                Text(
                    text =
                        "Konum izni verildi.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Harita açıldığında mevcut konumunuz hassas konumla bulunabilecek.",
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

            LocationPermissionLevel.APPROXIMATE -> {
                Text(
                    text =
                        "Yaklaşık konum izni verildi.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Harita yaklaşık bölgenize gidecek; teslimat noktasını haritada kendiniz netleştirebileceksiniz.",
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

            LocationPermissionLevel.NONE -> {
                Text(
                    text =
                        "Mevcut konumunuzu bulmak için konum izni gerekiyor.",
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
                        Modifier.height(10.dp)
                )

                if (permissionBlocked) {
                    OutlinedButton(
                        onClick = {
                            context
                                .openApplicationSettings()
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Uygulama Ayarlarını Aç"
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            /*
                             * Android 12+ için COARSE ve FINE birlikte
                             * istenir. Kullanıcı sistem penceresinden
                             * yaklaşık veya hassas konumu seçebilir.
                             */
                            permissionLauncher
                                .launch(
                                    arrayOf(
                                        Manifest.permission
                                            .ACCESS_COARSE_LOCATION,
                                        Manifest.permission
                                            .ACCESS_FINE_LOCATION
                                    )
                                )
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Konum İzni Ver"
                        )
                    }
                }
            }
        }
    }
}

fun Context.currentLocationPermissionLevel():
        LocationPermissionLevel {

    val hasFine =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission
                .ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    if (hasFine) {
        return LocationPermissionLevel.PRECISE
    }

    val hasCoarse =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission
                .ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    return if (hasCoarse) {
        LocationPermissionLevel.APPROXIMATE
    } else {
        LocationPermissionLevel.NONE
    }
}

private fun Context.openApplicationSettings() {
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        ).apply {
            data =
                Uri.fromParts(
                    "package",
                    packageName,
                    null
                )

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }

    startActivity(intent)
}

private tailrec fun Context.findActivity():
        Activity? {

    return when (this) {
        is Activity ->
            this

        is ContextWrapper ->
            baseContext.findActivity()

        else ->
            null
    }
}