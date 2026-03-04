package com.aventique.sephora.data.repository

import com.aventique.sephora.data.local.database.entity.ProductWithReviews
import com.aventique.sephora.data.local.datasource.ProductLocalDataSource
import com.aventique.sephora.data.mapper.toDomain
import com.aventique.sephora.data.remote.datasource.ProductRemoteDataSource
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {

    override fun getProducts(query: String, sortOrder: SortOrder): Flow<DataResult<List<Product>>> =
        flow {
            // Try to fetch from remote
            when (val remoteResult = remoteDataSource.getProductsWithReviews()) {
                is DataResult.Success -> {
                    // Save to local cache
                    localDataSource.saveProducts(remoteResult.data)
                    val processed = applyFilterAndSort(remoteResult.data, query, sortOrder)
                    emit(DataResult.Success(processed))
                }

                is DataResult.Failure -> {
                    // Try to fetch from local cache as fallback
                    when (val result = localDataSource.getAllProductsWithReviews()) {
                        is DataResult.Failure -> {
                            emit(DataResult.Failure(result.error))
                        }

                        is DataResult.Success<List<ProductWithReviews>> -> {
                            val products = result.data.map { it.toDomain() }
                            val processed = applyFilterAndSort(products, query, sortOrder)
                            emit(DataResult.Success(processed))
                        }
                    }
                }
            }
        }

    private fun applyFilterAndSort(
        items: List<Product>,
        query: String,
        sortOrder: SortOrder,
    ): List<Product> {
        val filtered = if (query.isBlank()) items else items.filter {
            it.name.contains(
                query,
                ignoreCase = true
            )
        }
        return filtered.map { product ->
            product.copy(
                reviews = when (sortOrder) {
                    SortOrder.BEST_TO_WORST -> product.reviews.sortedWith(compareByDescending {
                        it.rating ?: -Double.MAX_VALUE
                    })

                    SortOrder.WORST_TO_BEST -> product.reviews.sortedWith(compareBy {
                        it.rating ?: Double.MAX_VALUE
                    })
                }
            )
        }
    }

    override suspend fun getProductById(id: Long): DataResult<Product?> {
        return when (val result = remoteDataSource.getProductsWithReviews()) {
            is DataResult.Success -> {
                val found = result.data.find { it.id == id }
                DataResult.Success(found)
            }

            is DataResult.Failure -> {
                // Fallback to local data source
                when (val localResult = localDataSource.getProductById(id)) {
                    is DataResult.Success -> DataResult.Success(localResult.data?.toDomain())
                    is DataResult.Failure -> DataResult.Failure(localResult.error)
                }
            }
        }
    }
}

