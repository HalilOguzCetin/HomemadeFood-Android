package com.homemadefood.app.ui.recommendation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.AddressRepository
import com.homemadefood.app.data.repository.ProducerRecommendationRepository
import com.homemadefood.app.data.repository.CartRepository

class RecommendationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                RecommendationViewModel::class.java
            )
        ) {
            val addressRepository =
                AddressRepository()

            val recommendationRepository =
                ProducerRecommendationRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )
            val cartRepository =
                CartRepository()

            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(
                addressRepository =
                    addressRepository,

                recommendationRepository =
                    recommendationRepository,

                cartRepository =
                    cartRepository,

                sessionManager =
                    sessionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}