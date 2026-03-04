package com.aventique.sephora.data.repository

import com.aventique.sephora.data.local.datasource.ProductLocalDataSource
import com.aventique.sephora.data.mapper.toDomain
import com.aventique.sephora.data.remote.datasource.ProductRemoteDataSource
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {

    override fun getProducts(
        query: String,
        forceRefresh: Boolean
    ): Flow<DataResult<List<Product>>> =
        flow {
            // emit cache immediately if available
            val localResult = localDataSource.getAllProductsWithReviews()
            val hasCachedData = localResult is DataResult.Success && localResult.data.isNotEmpty()
            if (hasCachedData) {
                val cached = localResult.data.map { it.toDomain() }
                emit(DataResult.Success(applyFilter(cached, query)))
            }

            // fetch from remote only when forced or no cache
            if (forceRefresh || !hasCachedData) {
                when (val remoteResult = remoteDataSource.getProductsWithReviews()) {
                    is DataResult.Success -> {
                        localDataSource.saveProducts(remoteResult.data)
                        emit(DataResult.Success(applyFilter(remoteResult.data, query)))
                    }

                    is DataResult.Failure -> {
                        if (!hasCachedData) emit(DataResult.Failure(remoteResult.error))
                    }
                }
            }
        }

    private fun applyFilter(items: List<Product>, query: String): List<Product> =
        if (query.isBlank()) items else items.filter {
            it.name.contains(query, ignoreCase = true)
        }

    override suspend fun getProductById(id: Long): DataResult<Product?> {
        return when (val result = remoteDataSource.getProductsWithReviews()) {
            is DataResult.Success -> {
                val found = result.data.find { it.id == id }
                DataResult.Success(found)
            }

            is DataResult.Failure -> {
                when (val localResult = localDataSource.getProductById(id)) {
                    is DataResult.Success -> DataResult.Success(localResult.data?.toDomain())
                    is DataResult.Failure -> DataResult.Failure(localResult.error)
                }
            }
        }
    }
}
