package com.aventique.sephora.domain.usecase

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.repository.ProductRepository

class GetProductByIdUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(id: Long): DataResult<Product?> = productRepository.getProductById(id)
}
