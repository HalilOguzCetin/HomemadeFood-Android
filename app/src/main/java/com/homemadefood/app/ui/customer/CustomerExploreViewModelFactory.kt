package com.homemadefood.app.ui.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.DeliveryAddressSelectionManager
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.FoodRepository
import com.homemadefood.app.data.repository.StorefrontRepository

class CustomerExploreViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val applicationContext =
        context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CustomerExploreViewModel::class.java
            )
        ) {
            val sessionManager =
                SessionManager(
                    applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return CustomerExploreViewModel(
                categoryRepository =
                    CategoryRepository(),

                foodRepository =
                    FoodRepository(),

                storefrontRepository =
                    StorefrontRepository(),

                addressRepository =
                    AddressRepository(),

                deliveryAddressSelectionManager =
                    DeliveryAddressSelectionManager(
                        sessionManager
                    )
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}