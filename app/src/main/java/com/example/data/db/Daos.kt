package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.model.UserProfile
import com.example.data.model.PortfolioItem
import com.example.data.model.Recommendation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY id DESC")
    fun getAllPortfolioItems(): Flow<List<PortfolioItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioItem(item: PortfolioItem)

    @Update
    suspend fun updatePortfolioItem(item: PortfolioItem)

    @Query("DELETE FROM portfolio_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface RecommendationDao {
    @Query("SELECT * FROM recommendation_history ORDER BY id DESC")
    fun getAllHistoryFlow(): Flow<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(rec: Recommendation)
}
