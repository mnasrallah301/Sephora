package com.aventique.sephora.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aventique.sephora.data.local.database.entity.ProductEntity
import com.aventique.sephora.data.local.database.entity.ProductWithReviews
import com.aventique.sephora.data.local.database.entity.ReviewEntity
import com.aventique.sephora.domain.common.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithReviewsFlow(): Flow<List<ProductWithReviews>>

    @Transaction
    @Query("SELECT * FROM products")
    suspend fun getAllProductsWithReviews(): List<ProductWithReviews>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductWithReviewsById(id: Long): ProductWithReviews?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ")
    suspend fun searchProducts(query: String): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE product_id = :productId")
    suspend fun deleteReviewsForProduct(productId: Long)
}
