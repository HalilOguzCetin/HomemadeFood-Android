package com.homemadefood.app.ui.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.StorefrontRepository

class CustomerHomeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CustomerHomeViewModel::class.java
            )
        ) {
            val categoryRepository =
                CategoryRepository()

            val storefrontRepository =
                StorefrontRepository()

            val addressRepository =
                AddressRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            val deliveryAddressSelectionManager =
                DeliveryAddressSelectionManager(
                    sessionManager =
                        sessionManager
                )

            @Suppress("UNCHECKED_CAST")
            return CustomerHomeViewModel(
                categoryRepository =
                    categoryRepository,

                storefrontRepository =
                    storefrontRepository,

                addressRepository =
                    addressRepository,

                deliveryAddressSelectionManager =
                    deliveryAddressSelectionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}