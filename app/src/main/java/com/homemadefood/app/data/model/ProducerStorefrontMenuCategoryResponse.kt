package com.homemadefood.app.data.model

data class ProducerStorefrontMenuCategoryResponse(
    val categoryId: Int,
    val categoryName: String,
    val foods: List<ProducerStorefrontMenuFoodResponse>
)