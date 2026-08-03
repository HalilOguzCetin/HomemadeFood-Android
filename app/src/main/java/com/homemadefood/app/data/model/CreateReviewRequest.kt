package com.homemadefood.app.data.model

data class CreateReviewRequest(
    val orderId: Int,
    val rating: Int,
    val comment: String
)