package com.homemadefood.app.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureTokenCipher {

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "homemadefood_session_token_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val VERSION = "v1"
        const val GCM_TAG_LENGTH_BITS = 128
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    @Synchronized
    private fun getOrCreateKey(): SecretKey {
        val existingKey =
            keyStore.getKey(
                KEY_ALIAS,
                null
            ) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

        val keySpec =
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(
                    KeyProperties.BLOCK_MODE_GCM
                )
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }

    fun encrypt(
        plainText: String
    ): String {
        require(plainText.isNotBlank()) {
            "Şifrelenecek token boş olamaz."
        }

        val cipher =
            Cipher.getInstance(
                TRANSFORMATION
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateKey()
        )

        val encryptedBytes =
            cipher.doFinal(
                plainText.toByteArray(
                    Charsets.UTF_8
                )
            )

        val iv =
            Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )

        val cipherText =
            Base64.encodeToString(
                encryptedBytes,
                Base64.NO_WRAP
            )

        return "$VERSION.$iv.$cipherText"
    }

    fun decryptOrNull(
        encryptedValue: String?
    ): String? {
        if (encryptedValue.isNullOrBlank()) {
            return null
        }

        val parts =
            encryptedValue.split('.')

        if (
            parts.size != 3 ||
            parts[0] != VERSION
        ) {
            return null
        }

        return runCatching {
            val iv =
                Base64.decode(
                    parts[1],
                    Base64.NO_WRAP
                )

            val cipherText =
                Base64.decode(
                    parts[2],
                    Base64.NO_WRAP
                )

            val cipher =
                Cipher.getInstance(
                    TRANSFORMATION
                )

            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(
                    GCM_TAG_LENGTH_BITS,
                    iv
                )
            )

            String(
                cipher.doFinal(
                    cipherText
                ),
                Charsets.UTF_8
            )
        }.getOrNull()
    }
}