package com.aventique.sephora.domain.model

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val thumbnailUrl: String,
    val imageUrl: String,
    val brandName: String,
    val isProductSet: Boolean,
    val isSpecialBrand: Boolean,
    val averageRating: Double,
    val reviews: List<Review>,
    val hideReviews: Boolean,
)
