package com.aventique.sephora.domain.repository

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(query: String = "", sortOrder: SortOrder = SortOrder.BEST_TO_WORST): Flow<DataResult<List<Product>>>
    suspend fun getProductById(id: Long): DataResult<Product?>
}
