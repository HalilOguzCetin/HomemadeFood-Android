package com.homemadefood.app.ui.producer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.CategoryRepository
import com.homemadefood.app.data.repository.ProducerFoodRepository
import com.homemadefood.app.data.upload.FoodImageMultipartFactory

class EditFoodViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                EditFoodViewModel::class.java
            )
        ) {
            val producerFoodRepository =
                ProducerFoodRepository()

            val categoryRepository =
                CategoryRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            val foodImageMultipartFactory =
                FoodImageMultipartFactory(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return EditFoodViewModel(
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