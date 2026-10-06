package com.homemadefood.app.data.model

data class UpdateProducerApplicationStatusRequest(
    val status: String,
    val reviewNote: String? = null
)
