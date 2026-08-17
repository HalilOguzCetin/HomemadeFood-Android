package com.homemadefood.app.data.upload

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProducerBusinessImageMultipartFactory(
    context: Context
) {
    companion object {
        private const val MAX_IMAGE_SIZE_BYTES =
            5 * 1024 * 1024

        private const val BUFFER_SIZE =
            8 * 1024
    }

    private val contentResolver =
        context.applicationContext
            .contentResolver

    suspend fun createPart(
        uriString: String
    ): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val uri =
                runCatching {
                    Uri.parse(uriString)
                }.getOrNull()
                    ?: throw IllegalArgumentException(
                        "Seçilen işletme görseli açılamadı."
                    )

            val bytes =
                readImageBytes(uri)

            val imageType =
                detectImageType(bytes)
                    ?: throw IllegalArgumentException(
                        "Yalnızca JPG, PNG veya WEBP işletme görselleri yüklenebilir."
                    )

            ImageQualityValidator
                .validateMinimumResolution(
                    bytes = bytes,
                    imageLabel = "İşletme görseli"
                )

            val requestBody =
                bytes.toRequestBody(
                    imageType.mimeType
                        .toMediaType()
                )

            MultipartBody.Part.createFormData(
                name = "BusinessImage",
                filename =
                    "producer-business${imageType.extension}",
                body = requestBody
            )
        }

    private fun readImageBytes(
        uri: Uri
    ): ByteArray {
        val inputStream =
            contentResolver
                .openInputStream(uri)
                ?: throw IOException(
                    "Seçilen işletme görseli okunamadı."
                )

        inputStream.use { input ->
            val output =
                ByteArrayOutputStream()

            val buffer =
                ByteArray(BUFFER_SIZE)

            var totalRead = 0

            while (true) {
                val read =
                    input.read(buffer)

                if (read == -1) {
                    break
                }

                totalRead += read

                if (
                    totalRead >
                    MAX_IMAGE_SIZE_BYTES
                ) {
                    throw IllegalArgumentException(
                        "İşletme görseli en fazla 5 MB olabilir."
                    )
                }

                output.write(
                    buffer,
                    0,
                    read
                )
            }

            if (totalRead <= 0) {
                throw IllegalArgumentException(
                    "Seçilen işletme görseli boş veya okunamıyor."
                )
            }

            return output.toByteArray()
        }
    }

    private fun detectImageType(
        bytes: ByteArray
    ): ImageType? {
        val isJpeg =
            bytes.size >= 3 &&
                    bytes[0] == 0xFF.toByte() &&
                    bytes[1] == 0xD8.toByte() &&
                    bytes[2] == 0xFF.toByte()

        if (isJpeg) {
            return ImageType(
                mimeType = "image/jpeg",
                extension = ".jpg"
            )
        }

        val isPng =
            bytes.size >= 8 &&
                    bytes[0] == 0x89.toByte() &&
                    bytes[1] == 0x50.toByte() &&
                    bytes[2] == 0x4E.toByte() &&
                    bytes[3] == 0x47.toByte() &&
                    bytes[4] == 0x0D.toByte() &&
                    bytes[5] == 0x0A.toByte() &&
                    bytes[6] == 0x1A.toByte() &&
                    bytes[7] == 0x0A.toByte()

        if (isPng) {
            return ImageType(
                mimeType = "image/png",
                extension = ".png"
            )
        }

        val isWebP =
            bytes.size >= 12 &&
                    bytes[0] == 'R'.code.toByte() &&
                    bytes[1] == 'I'.code.toByte() &&
                    bytes[2] == 'F'.code.toByte() &&
                    bytes[3] == 'F'.code.toByte() &&
                    bytes[8] == 'W'.code.toByte() &&
                    bytes[9] == 'E'.code.toByte() &&
                    bytes[10] == 'B'.code.toByte() &&
                    bytes[11] == 'P'.code.toByte()

        if (isWebP) {
            return ImageType(
                mimeType = "image/webp",
                extension = ".webp"
            )
        }

        return null
    }

    private data class ImageType(
        val mimeType: String,
        val extension: String
    )
}