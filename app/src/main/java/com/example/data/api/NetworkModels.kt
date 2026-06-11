package com.example.data.api

import com.squareup.moshi.JsonClass

// --- Yahoo Finance API models ---
@JsonClass(generateAdapter = true)
data class YahooMeta(
    val symbol: String? = null,
    val regularMarketPrice: Double? = null,
    val previousClose: Double? = null
)

@JsonClass(generateAdapter = true)
data class YahooResult(
    val meta: YahooMeta? = null
)

@JsonClass(generateAdapter = true)
data class YahooChart(
    val result: List<YahooResult>? = null
)

@JsonClass(generateAdapter = true)
data class YahooResponse(
    val chart: YahooChart? = null
)

// --- Gemini API models ---
@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

// --- Structured Allocation response expected from Gemini ---
@JsonClass(generateAdapter = true)
data class AllocationResponse(
    val allocations: List<GeminiAllocation>,
    val expectedReturn: String,
    val riskWarning: String
)

@JsonClass(generateAdapter = true)
data class GeminiAllocation(
    val amount: Double,
    val name: String,
    val reason: String,
    val risk: String
)
