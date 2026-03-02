package com.aventique.sephora.data.remote.network

import com.aventique.sephora.data.remote.network.dto.ProductDto
import com.aventique.sephora.data.remote.network.dto.ProductReviewsDto
import retrofit2.http.GET

interface ProductsApi {
    @GET("items.json")
    suspend fun getItems(): List<ProductDto>

    @GET("reviews.json")
    suspend fun getReviews(): List<ProductReviewsDto>
}