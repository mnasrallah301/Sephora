package com.aventique.sephora.domain.usecase

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetAllProductsUseCase(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(
        query: String = "",
        sortOrder: SortOrder = SortOrder.BEST_TO_WORST,
    ): Flow<DataResult<List<Product>>> = productRepository.getProducts(query, sortOrder)
}

