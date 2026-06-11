package com.example.data.repository

import com.example.data.db.UserProfileDao
import com.example.data.db.PortfolioDao
import com.example.data.db.RecommendationDao
import com.example.data.model.UserProfile
import com.example.data.model.PortfolioItem
import com.example.data.model.Recommendation
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userProfileDao: UserProfileDao,
    private val portfolioDao: PortfolioDao,
    private val recommendationDao: RecommendationDao
) {
    val userProfileFlow: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()
    val portfolioFlow: Flow<List<PortfolioItem>> = portfolioDao.getAllPortfolioItems()
    val recommendationHistoryFlow: Flow<List<Recommendation>> = recommendationDao.getAllHistoryFlow()

    suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

    suspend fun addPortfolioItem(item: PortfolioItem) {
        portfolioDao.insertPortfolioItem(item)
    }

    suspend fun updatePortfolioItem(item: PortfolioItem) {
        portfolioDao.updatePortfolioItem(item)
    }

    suspend fun deletePortfolioItem(id: Int) {
        portfolioDao.deleteById(id)
    }

    suspend fun addRecommendation(rec: Recommendation) {
        recommendationDao.insertRecommendation(rec)
    }
}
