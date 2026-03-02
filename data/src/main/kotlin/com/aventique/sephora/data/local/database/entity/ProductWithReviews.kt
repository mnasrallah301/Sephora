package com.aventique.sephora.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithReviews(
    @Embedded
    val product: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val reviews: List<ReviewEntity>
)
