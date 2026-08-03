package com.homemadefood.app.data.model

data class ProducerApplicationRequest(
    val businessName: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val dailyCapacity: Int
)