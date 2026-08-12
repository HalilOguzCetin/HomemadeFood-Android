package com.homemadefood.app.ui.location

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.homemadefood.app.ui.address.SelectedLocation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun LocationMapScreen(
    onBackClick: () -> Unit,

    onConfirmLocation: (
        latitude: Double,
        longitude: Double
    ) -> Unit,

    initialSelectedLocation:
    SelectedLocation? = null,

    modifier: Modifier = Modifier
) {
    val context =
        LocalContext.current

    val fusedLocationClient =
        remember(context) {
            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }

    val hasInitialLocation =
        initialSelectedLocation
            ?.isValid() == true

    val initialMapTarget =
        remember(
            initialSelectedLocation
        ) {
            if (hasInitialLocation) {
                LatLng(
                    initialSelectedLocation!!
                        .latitude,
                    initialSelectedLocation
                        .longitude
                )
            } else {
                LatLng(
                    39.0,
                    35.0
                )
            }
        }

    val initialZoom =
        if (hasInitialLocation) {
            17f
        } else {
            5.5f
        }

    val cameraPositionState =
        rememberCameraPositionState {
            position =
                CameraPosition
                    .fromLatLngZoom(
                        initialMapTarget,
                        initialZoom
                    )
        }

    var isLocating by
    remember {
        mutableStateOf(
            !hasInitialLocation
        )
    }

    var locationError by
    remember {
        mutableStateOf<String?>(null)
    }

    /*
     * requestKey:
     * 0 -> ekranın ilk açılışı
     * >0 -> kullanıcı "Mevcut Konumuma Git"
     *       veya tekrar dene dedi.
     */
    var requestKey by
    remember {
        mutableIntStateOf(0)
    }

    val permissionLevel =
        context
            .currentLocationPermissionLevel()

    LaunchedEffect(
        requestKey,
        permissionLevel,
        initialSelectedLocation
    ) {
        /*
         * Edit ekranından geldiysek ilk açılışta
         * telefonun şu anki konumuna gitmiyoruz.
         * Harita doğrudan kayıtlı teslimat
         * koordinatında açılıyor.
         */
        if (
            requestKey == 0 &&
            hasInitialLocation
        ) {
            isLocating = false
            locationError = null
            return@LaunchedEffect
        }

        if (
            permissionLevel ==
            LocationPermissionLevel.NONE
        ) {
            isLocating = false
            locationError =
                "Mevcut konumu bulmak için konum izni gerekiyor. Haritadan teslimat noktasını yine de elle seçebilirsiniz."
            return@LaunchedEffect
        }

        if (!context.isLocationServiceEnabled()) {
            isLocating = false
            locationError =
                "Telefonunuzun konum hizmeti kapalı. Haritadan konumu elle seçebilir veya konumu açıp tekrar deneyebilirsiniz."
            return@LaunchedEffect
        }

        isLocating = true
        locationError = null

        try {
            val priority =
                if (
                    permissionLevel ==
                    LocationPermissionLevel.PRECISE
                ) {
                    Priority
                        .PRIORITY_HIGH_ACCURACY
                } else {
                    Priority
                        .PRIORITY_BALANCED_POWER_ACCURACY
                }

            val location =
                fusedLocationClient
                    .awaitCurrentLocation(
                        priority = priority
                    )

            if (location == null) {
                isLocating = false
                locationError =
                    "Mevcut konum alınamadı. Haritadan konumu elle seçebilir veya tekrar deneyebilirsiniz."
                return@LaunchedEffect
            }

            val latLng =
                LatLng(
                    location.latitude,
                    location.longitude
                )

            isLocating = false

            cameraPositionState.animate(
                update =
                    CameraUpdateFactory
                        .newLatLngZoom(
                            latLng,
                            17f
                        ),
                durationMs = 900
            )
        } catch (_: SecurityException) {
            isLocating = false
            locationError =
                "Konum izni bulunamadı. Haritadan teslimat noktasını elle seçebilirsiniz."
        } catch (_: Exception) {
            isLocating = false
            locationError =
                "Mevcut konum alınırken bir hata oluştu. Haritadan teslimat noktasını elle seçebilirsiniz."
        }
    }

    Scaffold(
        modifier = modifier,

        topBar = {
            TextButton(
                onClick = onBackClick
            ) {
                Text("← Geri")
            }
        }
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
        ) {
            GoogleMap(
                modifier =
                    Modifier.fillMaxSize(),

                cameraPositionState =
                    cameraPositionState,

                properties =
                    MapProperties(
                        isMyLocationEnabled =
                            permissionLevel !=
                                    LocationPermissionLevel.NONE
                    )
            )

            CenterLocationPin(
                modifier =
                    Modifier
                        .align(
                            Alignment.Center
                        )
                        .offset(
                            y = (-32).dp
                        )
            )

            if (isLocating) {
                StatusCard(
                    text =
                        "Mevcut konumunuz bulunuyor...",

                    showProgress = true,

                    modifier =
                        Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .padding(16.dp)
                )
            } else if (
                locationError != null
            ) {
                StatusCard(
                    text =
                        locationError.orEmpty(),

                    showProgress = false,

                    onRetryClick = {
                        requestKey++
                    },

                    modifier =
                        Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .padding(16.dp)
                )
            }

            Card(
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(16.dp)
                        .fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {
                    Text(
                        text =
                            "Teslimat noktasını seçin",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            if (hasInitialLocation) {
                                "Harita kayıtlı teslimat konumunda açıldı. Değiştirmek için haritayı hareket ettirin."
                            } else {
                                "Haritayı hareket ettirin ve teslimat noktasını ortadaki pinin altında bırakın."
                            },

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    if (hasInitialLocation) {
                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                requestKey++
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                !isLocating
                        ) {
                            Text(
                                "Mevcut Konumuma Git"
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(
                        onClick = {
                            val target =
                                cameraPositionState
                                    .position
                                    .target

                            onConfirmLocation(
                                target.latitude,
                                target.longitude
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            !cameraPositionState
                                .isMoving &&
                                    !isLocating
                    ) {
                        Text(
                            if (
                                cameraPositionState
                                    .isMoving ||
                                isLocating
                            ) {
                                "Konum belirleniyor..."
                            } else {
                                "Konumu Onayla"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterLocationPin(
    modifier: Modifier = Modifier
) {
    val pinColor =
        MaterialTheme
            .colorScheme
            .primary

    val centerColor =
        MaterialTheme
            .colorScheme
            .onPrimary

    Canvas(
        modifier =
            modifier.size(
                width = 52.dp,
                height = 64.dp
            )
    ) {
        val centerX =
            size.width / 2f

        val circleCenterY =
            size.height * 0.31f

        val radius =
            size.width * 0.30f

        val path =
            Path().apply {
                moveTo(
                    centerX -
                            radius * 0.62f,
                    circleCenterY +
                            radius * 0.55f
                )

                lineTo(
                    centerX +
                            radius * 0.62f,
                    circleCenterY +
                            radius * 0.55f
                )

                lineTo(
                    centerX,
                    size.height
                )

                close()
            }

        drawPath(
            path = path,
            color = pinColor
        )

        drawCircle(
            color = pinColor,
            radius = radius,
            center =
                Offset(
                    centerX,
                    circleCenterY
                )
        )

        drawCircle(
            color = centerColor,
            radius =
                radius * 0.35f,
            center =
                Offset(
                    centerX,
                    circleCenterY
                )
        )
    }
}

@Composable
private fun StatusCard(
    text: String,

    showProgress: Boolean,

    modifier: Modifier = Modifier,

    onRetryClick:
    (() -> Unit)? = null
) {
    Card(
        modifier = modifier,

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            if (showProgress) {
                CircularProgressIndicator()

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }

            Text(
                text = text,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            if (onRetryClick != null) {
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                TextButton(
                    onClick =
                        onRetryClick
                ) {
                    Text(
                        "Konumumu Tekrar Bul"
                    )
                }
            }
        }
    }
}

private suspend fun FusedLocationProviderClient
        .awaitCurrentLocation(
    priority: Int
): Location? =
    suspendCancellableCoroutine {
            continuation ->

        val cancellationTokenSource =
            CancellationTokenSource()

        continuation
            .invokeOnCancellation {
                cancellationTokenSource
                    .cancel()
            }

        try {
            getCurrentLocation(
                priority,
                cancellationTokenSource
                    .token
            )
                .addOnSuccessListener {
                        location ->

                    if (
                        continuation.isActive
                    ) {
                        continuation.resume(
                            location
                        )
                    }
                }
                .addOnFailureListener {
                        error ->

                    if (
                        continuation.isActive
                    ) {
                        continuation
                            .resumeWithException(
                                error
                            )
                    }
                }
        } catch (error: Exception) {
            if (
                continuation.isActive
            ) {
                continuation
                    .resumeWithException(
                        error
                    )
            }
        }
    }

private fun Context
        .isLocationServiceEnabled():
        Boolean {

    val locationManager =
        getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

    return if (
        Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.P
    ) {
        locationManager
            .isLocationEnabled
    } else {
        @Suppress("DEPRECATION")
        (
                locationManager
                    .isProviderEnabled(
                        LocationManager
                            .GPS_PROVIDER
                    ) ||
                        locationManager
                            .isProviderEnabled(
                                LocationManager
                                    .NETWORK_PROVIDER
                            )
                )
    }
}