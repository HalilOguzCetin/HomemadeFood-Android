package com.homemadefood.app.data.model

/**
 * Backend ile Android arasındaki ödeme yöntemi sözleşmesinin
 * tek kaynağı. API modelini String olarak tutmaya devam ediyoruz;
 * ancak karşılaştırma ve kullanıcıya gösterilen adlar burada çözülür.
 */
enum class PaymentMethod(
    val backendValue: String,
    val displayName: String
) {
    CASH_ON_DELIVERY(
        backendValue = "CashOnDelivery",
        displayName = "Kapıda Nakit"
    ),

    CARD_ON_DELIVERY(
        backendValue = "CardOnDelivery",
        displayName = "Kapıda Kart"
    ),

    UNKNOWN(
        backendValue = "",
        displayName = "Bilinmeyen Ödeme Yöntemi"
    );

    companion object {
        fun fromBackendValue(
            value: String?
        ): PaymentMethod {
            val normalizedValue =
                value?.trim()

            return entries.firstOrNull { method ->
                method != UNKNOWN &&
                        method.backendValue.equals(
                            normalizedValue,
                            ignoreCase = true
                        )
            } ?: UNKNOWN
        }

        fun displayNameFor(
            value: String?
        ): String {
            val method =
                fromBackendValue(value)

            return if (method == UNKNOWN) {
                value
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: UNKNOWN.displayName
            } else {
                method.displayName
            }
        }
    }
}

/**
 * Mevcut CreateOrder state/request sözleşmesini bozmadan eski sabit
 * kullanımını koruyan compatibility katmanı. Yeni gösterim/parse işlemleri
 * PaymentMethod üzerinden merkezi olarak yapılır.
 */
object PaymentMethods {
    const val CASH_ON_DELIVERY =
        "CashOnDelivery"

    const val CARD_ON_DELIVERY =
        "CardOnDelivery"

    fun isSupported(
        value: String?
    ): Boolean =
        PaymentMethod
            .fromBackendValue(value) !=
                PaymentMethod.UNKNOWN

    fun displayName(
        value: String?
    ): String =
        PaymentMethod
            .displayNameFor(value)
}