package com.homemadefood.app.data.upload

import android.graphics.BitmapFactory

object ImageQualityValidator {

    private const val MIN_SHORT_SIDE_PX = 600
    private const val MIN_LONG_SIDE_PX = 900

    fun validateMinimumResolution(
        bytes: ByteArray,
        imageLabel: String
    ) {
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            options
        )

        val width = options.outWidth
        val height = options.outHeight

        if (
            width <= 0 ||
            height <= 0
        ) {
            throw IllegalArgumentException(
                "$imageLabel çözünürlüğü okunamadı."
            )
        }

        val shortSide =
            minOf(
                width,
                height
            )

        val longSide =
            maxOf(
                width,
                height
            )

        if (
            shortSide < MIN_SHORT_SIDE_PX ||
            longSide < MIN_LONG_SIDE_PX
        ) {
            throw IllegalArgumentException(
                "$imageLabel yeterli çözünürlükte değil. " +
                        "Kısa kenar en az $MIN_SHORT_SIDE_PX px, " +
                        "uzun kenar en az $MIN_LONG_SIDE_PX px olmalıdır. " +
                        "Seçilen görsel: ${width}x${height} px."
            )
        }
    }
}