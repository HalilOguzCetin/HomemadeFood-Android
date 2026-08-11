package com.homemadefood.app.ui.address

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository

class AddressFormViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                AddressFormViewModel::class.java
            )
        ) {
            val applicationContext =
                context.applicationContext

            val addressRepository =
                AddressRepository()

            val sessionManager =
                SessionManager(
                    applicationContext
                )

            val reverseGeocoder =
                AddressReverseGeocoder(
                    applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return AddressFormViewModel(
                addressRepository =
                    addressRepository,

                sessionManager =
                    sessionManager,

                reverseGeocoder =
                    reverseGeocoder
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}