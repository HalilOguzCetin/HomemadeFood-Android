package com.homemadefood.app.data.model

data class ReviewResponse(
    val reviewId: Int,
    val orderId: Int,
    val producerProfileId: Int,
    val businessName: String,
    val customerFullName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)