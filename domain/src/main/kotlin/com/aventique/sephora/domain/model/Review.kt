package com.aventique.sephora.domain.model

data class Review(
    val reviewerName: String?,
    val text: String,
    val rating: Double?,
)
