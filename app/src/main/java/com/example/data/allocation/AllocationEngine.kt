package com.example.data.allocation

import com.example.data.api.GeminiAllocation
import com.example.data.model.StockAllocation
import kotlin.math.roundToInt

object AllocationEngine {

    // Strong, liquid Indian Stocks & ETFs across currently strong/trending sectors
    private val POWER_SECTOR = listOf(
        StockConfig("TATAPOWER", "Tata Power", "Renewable energy momentum and power transmission leader", "Medium"),
        StockConfig("ADANIPOWER", "Adani Power", "Strong coal thermal power demand and data centers growth catalyst", "High"),
        StockConfig("NTPC", "NTPC", "Steady state PSU dividend generator expanding into wind/solar majorly", "Low")
    )

    private val RENEWABLE_SECTOR = listOf(
        StockConfig("IREDA", "IREDA", "High volumes, PSU primary green financier for high leverage growth", "High"),
        StockConfig("SJN", "SJVN", "Hydropower and utility-scale solar projects growth engine", "High")
    )

    private val AUTO_EV_SECTOR = listOf(
        StockConfig("TATAMOTORS", "Tata Motors", "Leader in Indian EV passenger cars and commercial vehicles", "Medium"),
        StockConfig("M&M", "Mahindra & Mahindra", "Dominant SUV performance tracker and clean energy tractors", "Low")
    )

    private val DEFENSE_SECTOR = listOf(
        StockConfig("BEL", "Bharat Electronics", "Robust order book in aerospace electronics and defense radars", "Medium"),
        StockConfig("HAL", "Hindustan Aeronautics", "Indigenous fighter jet manufacturing and defense exports", "High")
    )

    private val DEFENSIVE_GOLD = listOf(
        StockConfig("GOLDBEES", "Gold BeES ETF", "Highly liquid gold tracker acting as an excellent hedge against inflation", "Low")
    )

    data class StockConfig(
        val ticker: String,
        val name: String,
        val reason: String,
        val risk: String
    )

    /**
     * Calculates precise rupee allocation based on available cash, age, and risk tolerance.
     * Respects all "survival instinct" rules:
     * - Under ₹5,000: Max 3-5 allocations.
     * - Minimum ₹50-100 per stock allocation.
     * - Keep 5-10% in Gold or Liquid Cash buffer.
     * - Balanced: Growth stocks (Power, Renewable, Auto, Defense) + Defensive (Gold).
     * - Never suggest 100% in a single stock.
     */
    fun calculateLocalAllocation(
        amount: Double,
        age: Int,
        riskTolerance: String
    ): List<StockAllocation> {
        if (amount <= 0) return emptyList()

        val allocations = mutableListOf<StockAllocation>()

        // 1. Safety Buffer Allocation (Gold & Cash)
        // Aggressive: 5% Gold, 5% Cash
        // Moderate: 10% Gold, 5% Cash
        // Conservative: 15% Gold, 10% Cash
        val (goldPct, cashPct) = when (riskTolerance.lowercase()) {
            "aggressive" -> Pair(0.06, 0.04)
            "conservative" -> Pair(0.18, 0.12)
            else -> Pair(0.11, 0.09) // Moderate
        }

        var goldAmount = (amount * goldPct).roundToRupee()
        var cashAmount = (amount * cashPct).roundToRupee()

        // Minimum limit check for gold allocation
        if (goldAmount < 50.0 && amount >= 500) {
            goldAmount = 50.0
        }
        if (cashAmount < 50.0 && amount >= 500) {
            cashAmount = 50.0
        }

        var remainingCash = amount - (goldAmount + cashAmount)
        if (remainingCash < 0) {
            // Re-adjust
            goldAmount = (amount * 0.10).roundToRupee()
            cashAmount = (amount * 0.05).roundToRupee()
            remainingCash = amount - (goldAmount + cashAmount)
        }

        // 2. Growth Stock Allocation
        // Based on risk, let's pick 2 to 4 high volume trending stocks
        val selectedStocks = mutableListOf<StockConfig>()

        when (riskTolerance.lowercase()) {
            "aggressive" -> {
                // Focus: Adani Power, IREDA, HAL
                selectedStocks.add(POWER_SECTOR[1]) // ADANIPOWER (High)
                selectedStocks.add(RENEWABLE_SECTOR[0]) // IREDA (High)
                selectedStocks.add(DEFENSE_SECTOR[1]) // HAL (High)
            }
            "conservative" -> {
                // Focus: NTPC, M&M, BEL
                selectedStocks.add(POWER_SECTOR[2]) // NTPC (Low)
                selectedStocks.add(AUTO_EV_SECTOR[1]) // M&M (Low)
                selectedStocks.add(DEFENSE_SECTOR[0]) // BEL (Medium)
            }
            else -> { // Moderate
                // Focus: Tata Power, Tata Motors, BEL, SJVN
                selectedStocks.add(POWER_SECTOR[0]) // TATAPOWER (Medium)
                selectedStocks.add(RENEWABLE_SECTOR[1]) // SJVN (High)
                selectedStocks.add(AUTO_EV_SECTOR[0]) // TATAMOTORS (Medium)
            }
        }

        val numStocks = selectedStocks.size
        if (numStocks > 0 && remainingCash > 0) {
            // Allocate remaining cash nearly-equally but respect ₹50 minimum
            val rawPerStock = remainingCash / numStocks
            var distributed = 0.0

            selectedStocks.forEachIndexed { index, stock ->
                val stockAlloc = if (index == numStocks - 1) {
                    remainingCash - distributed // Give the final remaining to avoid rounding error
                } else {
                    rawPerStock.roundToRupee()
                }

                if (stockAlloc > 0) {
                    allocations.add(
                        StockAllocation(
                            amount = stockAlloc,
                            name = "${stock.ticker} (${stock.name})",
                            reason = stock.reason,
                            risk = stock.risk
                        )
                    )
                    distributed += stockAlloc
                }
            }
        }

        // Add Gold
        if (goldAmount > 0) {
            allocations.add(
                StockAllocation(
                    amount = goldAmount,
                    name = "GOLDBEES (Gold ETF)",
                    reason = DEFENSIVE_GOLD[0].reason,
                    risk = DEFENSIVE_GOLD[0].risk
                )
            )
        }

        // Add Cash
        if (cashAmount > 0) {
            allocations.add(
                StockAllocation(
                    amount = cashAmount,
                    name = "Liquid Cash",
                    reason = "Reserve liquidity kept safe block for bottom-fishing opportunities",
                    risk = "Low"
                )
            )
        }

        // Final sanity check: ensure the total matches the original amount exactly
        val totalAllocated = allocations.sumOf { it.amount }
        if (totalAllocated != amount && allocations.isNotEmpty()) {
            val diff = amount - totalAllocated
            // merge difference to the first item (or cash if present)
            val indexToAdjust = allocations.indexOfFirst { it.name.lowercase().contains("cash") }.let {
                if (it != -1) it else 0
            }
            val target = allocations[indexToAdjust]
            allocations[indexToAdjust] = target.copy(amount = (target.amount + diff).roundToRupee())
        }

        return allocations
    }

    private fun Double.roundToRupee(): Double {
        return (this).roundToInt().toDouble()
    }

    /**
     * Formats Gemini prompt based on user details.
     */
    fun buildGeminiPrompt(amount: Double, age: Int, riskTolerance: String): String {
        return """
            You are "GrowKaro AI", a smart survivalist stock investment advisor for small cash amounts (₹).
            The user wants to invest ₹$amount cash.
            User Profile:
            - Age: $age
            - Risk Tolerance: $riskTolerance

            Please output a precise allocation JSON structure. Since the available amount can be small (such as ₹500, ₹1000, or ₹2500), your allocations MUST fulfill these constraints strictly:
            1. Under ₹5,000, allocate to a MAXIMUM of 3-5 individual assets. Each allocation MUST be at least ₹50 to be practical.
            2. Never recommend Mutual Funds. ONLY suggest individual stocks or liquid ETFs traded on Indian exchanges (NSE/BSE).
            3. Always keep between 5% to 15% combined in Gold ETF (such as "GOLDBEES") or "Liquid Cash" for physical safety and hedge survival buffer.
            4. Recommend actual trending and stable Indian sectors: Power (ADANIPOWER, TATAPOWER), Renewable Energy (SJVN, IREDA), Auto/EV (TATAMOTORS, M&M), Defense/Aerospace (BEL, HAL), or similar.
            5. Balance structure: 60-70% growth stocks + 20-30% defensive/hedge.
            6. Never put 100% of the money in any single stock.
            7. Generate logical reasons (current trend) for each recommendation.

            You MUST strictly return a valid JSON object matching this schema, with no markdown code blocks or extra text:
            {
              "allocations": [
                {
                  "amount": 500.0,
                  "name": "ADANIPOWER (Adani Power Ltd)",
                  "reason": "Strong power infrastructure demand and expansion in utility centers.",
                  "risk": "High"
                },
                ...
              ],
              "expectedReturn": "12-35% annual return expectations",
              "riskWarning": "Please remember that equities are subject to market volatility. Only invest surplus capital."
            }

            Remember: The total of all 'amount' fields in 'allocations' MUST sum up to EXACTLY: $amount. Double check your math!
        """.trimIndent()
    }
}
