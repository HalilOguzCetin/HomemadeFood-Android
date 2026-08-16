package com.homemadefood.app.ui.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AuthRepository

class CustomerPhoneVerificationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CustomerPhoneVerificationViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return CustomerPhoneVerificationViewModel(
                authRepository =
                    AuthRepository(),

                sessionManager =
                    SessionManager(
                        context.applicationContext
                    )
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}