package com.aventique.sephora.domain.usecase

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetAllProductsUseCase(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(query: String = "", forceRefresh: Boolean = false): Flow<DataResult<List<Product>>> =
        productRepository.getProducts(query, forceRefresh)
}
