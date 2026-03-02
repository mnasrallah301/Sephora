package com.aventique.sephora.data.remote.datasource

import com.aventique.sephora.data.common.safeCall
import com.aventique.sephora.data.mapper.toDomain
import com.aventique.sephora.data.remote.network.ProductsApi
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product

interface ProductRemoteDataSource {
    suspend fun getProducts(): DataResult<List<Product>>
    suspend fun getProductsWithReviews(): DataResult<List<Product>>
}

class ProductRemoteDataSourceImpl(private val api: ProductsApi) : ProductRemoteDataSource {
    
    override suspend fun getProducts(): DataResult<List<Product>> = safeCall {
        val items = api.getItems()
        items.map { it.toDomain(null) }
    }

    override suspend fun getProductsWithReviews(): DataResult<List<Product>> = safeCall {
        val items = api.getItems()
        val reviews = api.getReviews()
        val reviewsMap = reviews.associateBy { it.productId }
        items.toDomain(reviewsMap)
    }
}