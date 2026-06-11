package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.db.UserProfileDao
import com.example.data.db.PortfolioDao
import com.example.data.db.RecommendationDao
import com.example.data.model.UserProfile
import com.example.data.model.PortfolioItem
import com.example.data.model.Recommendation

@Database(
    entities = [UserProfile::class, PortfolioItem::class, Recommendation::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun recommendationDao(): RecommendationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "growkaro_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
