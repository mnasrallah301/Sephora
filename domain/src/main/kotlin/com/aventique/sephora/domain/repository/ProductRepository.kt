package com.aventique.sephora.domain.repository

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(query: String = "", forceRefresh: Boolean = false): Flow<DataResult<List<Product>>>
    suspend fun getProductById(id: Long): DataResult<Product?>
}
