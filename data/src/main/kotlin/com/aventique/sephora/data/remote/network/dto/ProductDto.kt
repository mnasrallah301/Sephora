package com.aventique.sephora.data.remote.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("product_id")
    val productId: Long,
    @SerialName("product_name")
    val productName: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("images_url")
    val imagesUrl: ImagesUrlDto? = null,
    @SerialName("c_brand")
    val brand: BrandDto? = null,
    @SerialName("is_productSet")
    val isProductSet: Boolean? = false,
    @SerialName("is_special_brand")
    val isSpecialBrand: Boolean? = false,
)

@Serializable
data class ImagesUrlDto(
    @SerialName("small")
    val small: String? = null,
    @SerialName("large")
    val large: String? = null,
)

@Serializable
data class BrandDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
)
