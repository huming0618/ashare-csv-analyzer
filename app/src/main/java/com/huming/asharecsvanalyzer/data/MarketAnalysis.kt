package com.huming.asharecsvanalyzer.data

data class HistogramBucket(
    val label: String,
    val minInclusive: Double,
    val maxExclusive: Double,
    val count: Int,
)

data class MarketAnalysis(
    val stockCount: Int,
    val tradeDate: String?,
    val upCount: Int,
    val downCount: Int,
    val flatCount: Int,
    val avgChangePercent: Double,
    val medianChangePercent: Double,
    val totalTurnover: Double,
    val totalVolume: Long,
    val topGainers: List<StockRow>,
    val topLosers: List<StockRow>,
    val topByTurnover: List<StockRow>,
    val topByTurnoverRatio: List<StockRow>,
    val histogram: List<HistogramBucket>,
)
