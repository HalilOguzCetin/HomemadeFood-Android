package com.homemadefood.app.data.model

data class AddCartItemRequest(
    val foodId: Int,
    val quantity: Int,
    val recommendationSearchId: Int? = null
)