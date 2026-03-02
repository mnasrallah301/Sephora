package com.aventique.sephora.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aventique.sephora.data.local.database.dao.ProductDao
import com.aventique.sephora.data.local.database.entity.ProductEntity
import com.aventique.sephora.data.local.database.entity.ReviewEntity

@Database(
    entities = [ProductEntity::class, ReviewEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SephoraDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var instance: SephoraDatabase? = null

        fun getInstance(context: Context): SephoraDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SephoraDatabase::class.java,
                    "sephora_database",
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}