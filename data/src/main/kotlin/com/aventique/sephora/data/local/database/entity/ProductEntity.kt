package com.aventique.sephora.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "brand_name")
    val brandName: String,
    @ColumnInfo(name = "is_product_set")
    val isProductSet: Boolean,
    @ColumnInfo(name = "is_special_brand")
    val isSpecialBrand: Boolean,
    @ColumnInfo(name = "average_rating")
    val averageRating: Double,
    @ColumnInfo(name = "review_count")
    val reviewCount: Int,
    @ColumnInfo(name = "hide_reviews")
    val hideReviews: Boolean,
)
