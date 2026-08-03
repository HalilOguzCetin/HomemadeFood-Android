package com.homemadefood.app.ui.producer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.ProducerRepository
import com.homemadefood.app.data.repository.ReviewRepository

class ProducerReviewsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ProducerReviewsViewModel::class.java
            )
        ) {
            val producerRepository =
                ProducerRepository()

            val reviewRepository =
                ReviewRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return ProducerReviewsViewModel(
                producerRepository =
                    producerRepository,

                reviewRepository =
                    reviewRepository,

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