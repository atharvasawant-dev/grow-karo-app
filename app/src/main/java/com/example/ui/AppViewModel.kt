package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.allocation.AllocationEngine
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerateContentResponse
import com.example.data.api.Content
import com.example.data.api.Part
import com.example.data.api.GenerationConfig
import com.example.data.api.NetworkClient
import com.example.data.api.AllocationResponse
import com.example.data.db.AppDatabase
import com.example.data.model.PortfolioItem
import com.example.data.model.Recommendation
import com.example.data.model.StockAllocation
import com.example.data.model.UserProfile
import com.example.data.repository.AppRepository
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        db.userProfileDao(),
        db.portfolioDao(),
        db.recommendationDao()
    )

    // UI State flows
    val userProfileFlow = repository.userProfileFlow
    val portfolioFlow = repository.portfolioFlow
    val recommendationHistoryFlow = repository.recommendationHistoryFlow

    private val _niftyPrice = MutableStateFlow(22415.80)
    val niftyPrice: StateFlow<Double> = _niftyPrice.asStateFlow()

    private val _niftyChange = MutableStateFlow(88.50)
    val niftyChange: StateFlow<Double> = _niftyChange.asStateFlow()

    private val _niftyChangePercent = MutableStateFlow(0.40)
    val niftyChangePercent: StateFlow<Double> = _niftyChangePercent.asStateFlow()

    private val _isAllocationLoading = MutableStateFlow(false)
    val isAllocationLoading: StateFlow<Boolean> = _isAllocationLoading.asStateFlow()

    private val _allocationError = MutableStateFlow<String?>(null)
    val allocationError: StateFlow<String?> = _allocationError.asStateFlow()

    private val _currentRecommendation = MutableStateFlow<Recommendation?>(null)
    val currentRecommendation: StateFlow<Recommendation?> = _currentRecommendation.asStateFlow()

    private val _currentAllocations = MutableStateFlow<List<StockAllocation>>(emptyList())
    val currentAllocations: StateFlow<List<StockAllocation>> = _currentAllocations.asStateFlow()

    init {
        // Run initial configuration sync and fetch Nifty level
        viewModelScope.launch {
            ensureInitialProfileExists()
            fetchNiftyLevel()
            loadLatestRecommendation()
        }
    }

    private suspend fun ensureInitialProfileExists() {
        val existing = repository.getUserProfile()
        if (existing == null) {
            repository.saveUserProfile(UserProfile(1, 25, "Moderate", ""))
        }
    }

    private suspend fun loadLatestRecommendation() {
        val history = repository.recommendationHistoryFlow.firstOrNull() ?: emptyList()
        val latest = history.firstOrNull()
        if (latest != null) {
            _currentRecommendation.value = latest
            try {
                val moshi = NetworkClient.getMoshi()
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, StockAllocation::class.java)
                val adapter = moshi.adapter<List<StockAllocation>>(listType)
                _currentAllocations.value = adapter.fromJson(latest.allocationsJson) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchNiftyLevel() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.yahooService.getNiftyData()
                val result = response.chart?.result?.firstOrNull()
                val meta = result?.meta
                val price = meta?.regularMarketPrice
                val prevClose = meta?.previousClose
                if (price != null && price > 0) {
                    _niftyPrice.value = price
                    if (prevClose != null && prevClose > 0) {
                        val change = price - prevClose
                        val pct = (change / prevClose) * 100.0
                        _niftyChange.value = change
                        _niftyChangePercent.value = pct
                    }
                }
            } catch (e: Exception) {
                // Keep simulated/fallback pre-cached default Nifty index level if network is offline
                e.printStackTrace()
            }
        }
    }

    fun saveProfile(age: Int, riskTolerance: String, customApiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserProfile(UserProfile(1, age, riskTolerance, customApiKey))
        }
    }

    fun generateAllocation(amount: Double) {
        if (amount <= 0) return
        _isAllocationLoading.value = true
        _allocationError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val profile = repository.getUserProfile() ?: UserProfile(1, 25, "Moderate", "")
            val storedKey = profile.customApiKey
            
            // Trim API keys to handle any accidentally copy-pasted spaces or new lines
            val isCustomKey = storedKey.isNotBlank()
            val apiKey = if (isCustomKey) {
                storedKey.trim()
            } else {
                BuildConfig.GEMINI_API_KEY.trim()
            }

            // We use the universally supported and stable 'gemini-1.5-flash' model
            // for both platform keys and custom user keys to avoid HTTP 403 or 401 errors.
            val modelName = "gemini-1.5-flash"

            // Check if key is empty or default placeholder
            val useLocalEngine = apiKey.isBlank() || 
                    apiKey == "MY_GEMINI_API_KEY" || 
                    apiKey.lowercase().contains("placeholder")

            if (useLocalEngine) {
                // Fast Local Survival Engine Fallback
                val localAlloc = AllocationEngine.calculateLocalAllocation(amount, profile.age, profile.riskTolerance)
                val expectedReturn = when (profile.riskTolerance.lowercase()) {
                    "aggressive" -> "18-35% expected annual growth"
                    "conservative" -> "8-15% slow organic safety"
                    else -> "12-24% balanced moderate CAGR"
                }
                val warning = "Disclaimer: Allocations calculated based on GrowKaro AI local survival parameters. Stocks carry volatility. Please research."
                updateAllocationState(amount, localAlloc, expectedReturn, warning)
            } else {
                // Call Gemini API directly (Structured Output via REST)
                try {
                    val promptText = AllocationEngine.buildGeminiPrompt(amount, profile.age, profile.riskTolerance)
                    val request = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(Part(text = promptText)))),
                        generationConfig = GenerationConfig(
                            responseMimeType = "application/json",
                            temperature = 0.4f
                        )
                    )

                    val response = NetworkClient.geminiService.generateContent(modelName, apiKey, request)
                    val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (jsonText != null) {
                        var cleanedJson = jsonText.trim()
                        if (cleanedJson.startsWith("```")) {
                            // Strip leading line (e.g. ```json or ```)
                            val firstNewLine = cleanedJson.indexOf('\n')
                            if (firstNewLine != -1) {
                                cleanedJson = cleanedJson.substring(firstNewLine).trim()
                            }
                            if (cleanedJson.endsWith("```")) {
                                cleanedJson = cleanedJson.substring(0, cleanedJson.length - 3).trim()
                            }
                        }

                        val moshi = NetworkClient.getMoshi()
                        val adapter = moshi.adapter(AllocationResponse::class.java)
                        val parseResult = adapter.fromJson(cleanedJson)

                        if (parseResult != null && parseResult.allocations.isNotEmpty()) {
                            // Turn Gemini models to App models
                            val mappedAllocations = parseResult.allocations.map { gemini ->
                                StockAllocation(
                                    amount = gemini.amount,
                                    name = gemini.name,
                                    reason = gemini.reason,
                                    risk = gemini.risk
                                )
                            }
                            updateAllocationState(amount, mappedAllocations, parseResult.expectedReturn, parseResult.riskWarning)
                        } else {
                            throw Exception("Failed to parse correct allocation configuration from response")
                        }
                    } else {
                        throw Exception("Empty content response returned from AI model")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback automatically to robust local allocation engine on any network failure
                    val localAlloc = AllocationEngine.calculateLocalAllocation(amount, profile.age, profile.riskTolerance)
                    val expectedReturn = when (profile.riskTolerance.lowercase()) {
                        "aggressive" -> "18-35% expected annual growth"
                        "conservative" -> "8-15% slow organic safety"
                        else -> "12-24% balanced moderate CAGR"
                    }
                    val errorDetail = e.localizedMessage ?: e.message ?: e.javaClass.simpleName
                    val warning = "Note: Network/API failed ($errorDetail). Applied offline local rules engine as robust safety buffer."
                    updateAllocationState(amount, localAlloc, expectedReturn, warning)
                }
            }
        }
    }

    private suspend fun updateAllocationState(
        amount: Double,
        allocations: List<StockAllocation>,
        expectedReturn: String,
        riskWarning: String
    ) {
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val moshi = NetworkClient.getMoshi()
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, StockAllocation::class.java)
        val adapter = moshi.adapter<List<StockAllocation>>(listType)
        val jsonValue = adapter.toJson(allocations)

        val recommendation = Recommendation(
            amount = amount,
            dateStr = dateStr,
            allocationsJson = jsonValue,
            expectedReturn = expectedReturn,
            riskWarning = riskWarning
        )

        // Save recommendation to local database
        repository.addRecommendation(recommendation)

        withContext(Dispatchers.Main) {
            _currentRecommendation.value = recommendation
            _currentAllocations.value = allocations
            _isAllocationLoading.value = false
        }
    }

    // Portfolio Management
    fun buyAsset(ticker: String, name: String, amountInvested: Double, buyPrice: Double, quantity: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            // Add custom transaction to Room DB
            val item = PortfolioItem(
                stockName = name,
                ticker = ticker.uppercase(Locale.ROOT),
                amountInvested = amountInvested,
                buyPrice = buyPrice,
                currentPrice = buyPrice, // Assume initially the same price, but will simulate update
                quantity = quantity,
                dateBought = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.addPortfolioItem(item)
        }
    }

    fun deletePortfolioItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePortfolioItem(id)
        }
    }

    // Fast simulation trigger to update portfolio values (e.g., fluctuation simulator for P/L)
    fun simulateMarketFluctuation() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = portfolioFlow.firstOrNull() ?: emptyList()
            list.forEach { item ->
                // random fluctuation between -4% and +6%
                val factor = 0.96 + (Math.random() * 0.10)
                val newPrice = (item.buyPrice * factor * 100.0).roundToInt() / 100.0
                val updatedItem = item.copy(currentPrice = newPrice)
                repository.updatePortfolioItem(updatedItem)
            }
        }
    }

    fun clearHistoryAndData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Wipe Room DB table data
            db.clearAllTables()
            // Reset profile table
            ensureInitialProfileExists()
        }
    }
}
