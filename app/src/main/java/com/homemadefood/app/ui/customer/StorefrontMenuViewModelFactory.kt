package com.homemadefood.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.repository.StorefrontRepository

class StorefrontMenuViewModelFactory(
    private val producerProfileId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                StorefrontMenuViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return StorefrontMenuViewModel(
                producerProfileId =
                    producerProfileId,

                storefrontRepository =
                    StorefrontRepository()
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}