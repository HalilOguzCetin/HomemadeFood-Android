package com.homemadefood.app.data.model

data class ProducerBusinessHourResponse(
    val dayOfWeek: Int,
    val dayName: String,
    val isClosed: Boolean,
    val openTime: String?,
    val closeTime: String?
)
