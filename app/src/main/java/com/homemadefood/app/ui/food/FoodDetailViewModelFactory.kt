package com.homemadefood.app.ui.food

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homemadefood.app.data.local.SessionManager
import com.homemadefood.app.data.repository.FavoriteRepository
import com.homemadefood.app.data.repository.FoodRepository

class FoodDetailViewModelFactory(
    private val context: Context,
    private val foodId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                FoodDetailViewModel::class.java
            )
        ) {
            val foodRepository =
                FoodRepository()

            val favoriteRepository =
                FavoriteRepository()

            val sessionManager =
                SessionManager(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return FoodDetailViewModel(
                foodId = foodId,

                foodRepository =
                    foodRepository,

                favoriteRepository =
                    favoriteRepository,

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