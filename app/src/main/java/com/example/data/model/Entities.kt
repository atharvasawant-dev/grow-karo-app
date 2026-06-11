package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Store a single profile row
    val age: Int = 25,
    val riskTolerance: String = "Moderate", // "Conservative", "Moderate", "Aggressive"
    val customApiKey: String = ""
)

@Entity(tableName = "portfolio_items")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stockName: String,
    val ticker: String,
    val amountInvested: Double,
    val buyPrice: Double,
    val currentPrice: Double,
    val quantity: Double,
    val dateBought: String
)

@Entity(tableName = "recommendation_history")
data class Recommendation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val dateStr: String,
    val allocationsJson: String, // JSON array string of list of StockAllocation
    val expectedReturn: String = "12-35%",
    val riskWarning: String = ""
)

data class StockAllocation(
    val amount: Double,
    val name: String,
    val reason: String,
    val risk: String // "Low", "Medium", "High"
)
