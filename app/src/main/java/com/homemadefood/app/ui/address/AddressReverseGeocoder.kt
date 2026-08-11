package com.homemadefood.app.ui.address

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ReverseGeocodedAddress(
    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val buildingNo: String = "",
    val formattedAddress: String = ""
)

class AddressReverseGeocoder(
    context: Context
) {
    private val applicationContext =
        context.applicationContext

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): Result<ReverseGeocodedAddress> {
        if (!Geocoder.isPresent()) {
            return Result.failure(
                IllegalStateException(
                    "Bu cihazda adres çözümleme servisi bulunmuyor."
                )
            )
        }

        return runCatching {
            val geocoder =
                Geocoder(
                    applicationContext,
                    Locale("tr", "TR")
                )

            val addresses =
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.TIRAMISU
                ) {
                    geocoder.awaitFromLocation(
                        latitude = latitude,
                        longitude = longitude
                    )
                } else {
                    withContext(Dispatchers.IO) {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(
                            latitude,
                            longitude,
                            1
                        ).orEmpty()
                    }
                }

            val address =
                addresses.firstOrNull()
                    ?: throw IOException(
                        "Seçilen konum için adres bilgisi bulunamadı."
                    )

            address.toReverseGeocodedAddress()
        }
    }
}

private fun Address
        .toReverseGeocodedAddress():
        ReverseGeocodedAddress {

    val cityValue =
        adminArea
            ?.trim()
            .orEmpty()
            .ifBlank {
                locality
                    ?.trim()
                    .orEmpty()
            }

    val districtValue =
        subAdminArea
            ?.trim()
            .orEmpty()
            .ifBlank {
                locality
                    ?.trim()
                    .orEmpty()
                    .takeUnless {
                        it.equals(
                            cityValue,
                            ignoreCase = true
                        )
                    }
                    .orEmpty()
            }

    return ReverseGeocodedAddress(
        city = cityValue,

        district = districtValue,

        neighborhood =
            subLocality
                ?.trim()
                .orEmpty(),

        street =
            thoroughfare
                ?.trim()
                .orEmpty(),

        buildingNo =
            subThoroughfare
                ?.trim()
                .orEmpty(),

        formattedAddress =
            getAddressLine(0)
                ?.trim()
                .orEmpty()
    )
}

private suspend fun Geocoder
        .awaitFromLocation(
    latitude: Double,
    longitude: Double
): List<Address> =
    suspendCancellableCoroutine {
            continuation ->

        getFromLocation(
            latitude,
            longitude,
            1,
            object : Geocoder.GeocodeListener {

                override fun onGeocode(
                    addresses: MutableList<Address>
                ) {
                    if (
                        continuation.isActive
                    ) {
                        continuation.resume(
                            addresses
                        )
                    }
                }

                override fun onError(
                    errorMessage: String?
                ) {
                    if (
                        continuation.isActive
                    ) {
                        continuation
                            .resumeWithException(
                                IOException(
                                    errorMessage
                                        ?.takeIf {
                                            it.isNotBlank()
                                        }
                                        ?: "Adres bilgisi çözümlenemedi."
                                )
                            )
                    }
                }
            }
        )
    }