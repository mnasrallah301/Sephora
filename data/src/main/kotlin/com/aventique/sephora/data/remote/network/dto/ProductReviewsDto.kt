package com.aventique.sephora.data.remote.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductReviewsDto(
    @SerialName("product_id")
    val productId: Long,
    @SerialName("hide")
    val hide: Boolean? = false,
    @SerialName("reviews")
    val reviews: List<ReviewDto>? = null,
)

@Serializable
data class ReviewDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("rating")
    val rating: Double? = null,
)
