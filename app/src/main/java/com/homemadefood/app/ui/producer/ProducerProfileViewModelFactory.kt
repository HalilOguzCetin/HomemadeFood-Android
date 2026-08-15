package com.homemadefood.app.ui.producer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.ProducerRepository
import com.homemadefood.app.data.upload.ProducerBusinessImageMultipartFactory

class ProducerProfileViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ProducerProfileViewModel::class.java
            )
        ) {
            val applicationContext =
                context.applicationContext

            val producerRepository =
                ProducerRepository()

            val addressRepository =
                AddressRepository()

            val sessionManager =
                SessionManager(
                    applicationContext
                )

            val producerBusinessImageMultipartFactory =
                ProducerBusinessImageMultipartFactory(
                    applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return ProducerProfileViewModel(
                producerRepository =
                    producerRepository,

                addressRepository =
                    addressRepository,

                sessionManager =
                    sessionManager,

                producerBusinessImageMultipartFactory =
                    producerBusinessImageMultipartFactory
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}