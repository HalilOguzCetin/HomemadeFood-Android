package com.homemadefood.app.ui.producer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.ProducerFoodRepository
import com.homemadefood.app.data.upload.FoodImageMultipartFactory

class CreateFoodViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                CreateFoodViewModel::class.java
            )
        ) {
            val applicationContext =
                context.applicationContext

            val producerFoodRepository =
                ProducerFoodRepository()

            val categoryRepository =
                CategoryRepository()

            val sessionManager =
                SessionManager(
                    applicationContext
                )

            val foodImageMultipartFactory =
                FoodImageMultipartFactory(
                    applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return CreateFoodViewModel(
                producerFoodRepository =
                    producerFoodRepository,

                categoryRepository =
                    categoryRepository,

                sessionManager =
                    sessionManager,

                foodImageMultipartFactory =
                    foodImageMultipartFactory
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: " +
                    modelClass.name
        )
    }
}