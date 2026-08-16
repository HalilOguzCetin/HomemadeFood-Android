package com.homemadefood.app.data.model

data class UserProfileResponse(
    val userId: Int,
    val fullName: String,
    val email: String,

    /*
     * C2A backend artık mevcut telefon değerini
     * profile response içinde döndürüyor.
     *
     * Telefon doğrulama/değiştirme C3'te OTP ile
     * ayrı güvenlik akışı olarak tamamlanacak.
     */
    val phone: String = "",

    val isPhoneVerified: Boolean = false,

    val phoneVerifiedAt: String? = null,

    val isEmailVerified: Boolean = false,

    val emailVerifiedAt: String? = null,

    val role: String,

    val canUseProducerMode: Boolean = false,

    val producerProfileId: Int? = null,

    val producerVerificationStatus: String? = null
)