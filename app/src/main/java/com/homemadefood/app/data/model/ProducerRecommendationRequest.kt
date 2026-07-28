package com.homemadefood.app.data.model

data class ProducerRecommendationRequest(
    val searchText: String,
    val addressId: Int,
    val quantity: Int?
)