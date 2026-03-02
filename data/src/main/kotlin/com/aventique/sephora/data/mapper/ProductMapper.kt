package com.aventique.sephora.data.mapper

import com.aventique.sephora.data.local.database.entity.ProductEntity
import com.aventique.sephora.data.local.database.entity.ProductWithReviews
import com.aventique.sephora.data.local.database.entity.ReviewEntity
import com.aventique.sephora.data.remote.network.dto.ProductDto
import com.aventique.sephora.data.remote.network.dto.ProductReviewsDto
import com.aventique.sephora.data.remote.network.dto.ReviewDto
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.model.Review

fun List<ProductDto>.toDomain(reviewsMap: Map<Long, ProductReviewsDto>): List<Product> =
    map { it.toDomain(reviewsMap[it.productId]) }

fun ProductDto.toDomain(reviewsDto: ProductReviewsDto?): Product {
    val reviews = reviewsDto?.reviews?.map { it.toDomain() } ?: emptyList()
    val validRatings = reviews.mapNotNull { it.rating }.filter { it in 0.0..5.0 }
    val avgRating = if (validRatings.isEmpty()) 0.0 else validRatings.average()
    return Product(
        id = productId,
        name = productName.orEmpty(),
        description = description.orEmpty(),
        price = price ?: 0.0,
        thumbnailUrl = imagesUrl?.small.orEmpty(),
        imageUrl = imagesUrl?.large.orEmpty(),
        brandName = brand?.name.orEmpty(),
        isProductSet = isProductSet ?: false,
        isSpecialBrand = isSpecialBrand ?: false,
        averageRating = avgRating,
        reviews = reviews,
        hideReviews = reviewsDto?.hide ?: false,
    )
}

fun ReviewDto.toDomain() = Review(
    reviewerName = name,
    text = text.orEmpty(),
    rating = rating ?: 0.0,
)

fun ProductEntity.toDomain(reviews: List<ReviewEntity> = emptyList()): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    thumbnailUrl = thumbnailUrl,
    imageUrl = imageUrl,
    brandName = brandName,
    isProductSet = isProductSet,
    isSpecialBrand = isSpecialBrand,
    averageRating = averageRating,
    reviews = reviews.map { it.toDomain() },
    hideReviews = hideReviews,
)

fun ReviewEntity.toDomain(): Review = Review(
    reviewerName = reviewerName,
    text = text,
    rating = rating,
)

fun ProductWithReviews.toDomain(): Product = product.toDomain(reviews)

fun List<ProductWithReviews>.toDomain(): List<Product> = map { it.toDomain() }

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    thumbnailUrl = thumbnailUrl,
    imageUrl = imageUrl,
    brandName = brandName,
    isProductSet = isProductSet,
    isSpecialBrand = isSpecialBrand,
    averageRating = averageRating,
    reviewCount = reviews.size,
    hideReviews = hideReviews,
)

fun Review.toEntity(productId: Long): ReviewEntity = ReviewEntity(
    productId = productId,
    reviewerName = reviewerName,
    text = text,
    rating = rating,
)

fun List<Review>.toEntity(productId: Long): List<ReviewEntity> = map { it.toEntity(productId) }

fun List<Product>.toEntity(): List<ProductEntity> = map { it.toEntity() }