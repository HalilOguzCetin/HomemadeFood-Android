package com.homemadefood.app.ui.customer

data class CustomerPhoneVerificationUiState(
    val phone: String = "",
    val code: String = "",

    val isRequestingCode: Boolean = false,
    val isVerifying: Boolean = false,

    val isCodeSent: Boolean = false,

    /*
     * Her başarılı request/resend işleminde artar.
     * UI bu değeri izleyip 60 saniyelik countdown'u
     * yeniden başlatır.
     */
    val codeRequestVersion: Int = 0,

    val isVerificationCompleted: Boolean = false,

    val message: String? = null,
    val isError: Boolean = false
) {
    val isBusy: Boolean
        get() =
            isRequestingCode ||
                    isVerifying

    val canRequestCode: Boolean
        get() =
            !isBusy &&
                    isLikelyTurkishMobilePhone(
                        phone
                    )

    val canVerify: Boolean
        get() =
            !isBusy &&
                    isCodeSent &&
                    code.length == 6

    companion object {
        fun isLikelyTurkishMobilePhone(
            value: String
        ): Boolean {
            val digits =
                value.filter {
                    it.isDigit()
                }

            val nationalNumber =
                when {
                    digits.length == 14 &&
                            digits.startsWith("0090") ->
                        digits.drop(4)

                    digits.length == 12 &&
                            digits.startsWith("90") ->
                        digits.drop(2)

                    digits.length == 11 &&
                            digits.startsWith("0") ->
                        digits.drop(1)

                    digits.length == 10 ->
                        digits

                    else ->
                        return false
                }

            return nationalNumber.length == 10 &&
                    nationalNumber.startsWith("5")
        }
    }
}