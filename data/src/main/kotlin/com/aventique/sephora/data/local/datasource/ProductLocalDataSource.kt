package com.aventique.sephora.data.local.datasource

import com.aventique.sephora.data.common.safeCall
import com.aventique.sephora.data.local.database.dao.ProductDao
import com.aventique.sephora.data.local.database.entity.ProductWithReviews
import com.aventique.sephora.data.mapper.toEntity
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {
    fun getAllProductsWithReviewsFlow(): Flow<List<ProductWithReviews>>
    suspend fun getAllProductsWithReviews(): DataResult<List<ProductWithReviews>>
    suspend fun getProductById(id: Long): DataResult<ProductWithReviews?>
    suspend fun saveProducts(products: List<Product>): DataResult<Unit>
}


class ProductLocalDataSourceImpl(private val dao: ProductDao) : ProductLocalDataSource {

    override fun getAllProductsWithReviewsFlow(): Flow<List<ProductWithReviews>> =
        dao.getAllProductsWithReviewsFlow()

    override suspend fun getAllProductsWithReviews(): DataResult<List<ProductWithReviews>>  = safeCall {
         dao.getAllProductsWithReviews()
    }

    override suspend fun getProductById(id: Long): DataResult<ProductWithReviews?> = safeCall {
        dao.getProductWithReviewsById(id)
    }

    override suspend fun saveProducts(products: List<Product>): DataResult<Unit> = safeCall {
        // insert products and their reviews; clear existing reviews for the products first
        products.forEach { product ->
            dao.deleteReviewsForProduct(product.id)
        }
        dao.insertProducts(products.toEntity())
        // gather review entities
        val reviewEntities = products.flatMap { it.reviews.toEntity(it.id) }
        if (reviewEntities.isNotEmpty()) {
            dao.insertReviews(reviewEntities)
        }
    }
}
