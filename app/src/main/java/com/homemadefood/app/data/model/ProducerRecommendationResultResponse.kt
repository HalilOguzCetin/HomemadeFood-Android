package com.homemadefood.app.data.model

data class ProducerRecommendationResultResponse(
    val recommendationSearchId: Int,
    val recommendations:
    List<ProducerRecommendationResponse>
)