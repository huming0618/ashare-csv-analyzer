package com.huming.asharecsvanalyzer.data

data class StockRow(
    val fullCode: String,
    val code: String,
    val name: String,
    val lastPrice: Double,
    val changeAmount: Double,
    val changePercent: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val prevClose: Double,
    val volume: Long,
    val turnover: Double,
    val turnoverRatio: Double,
    val pe: Double?,
    val pb: Double?,
    val totalMarketCapWan: Double?,
    val floatMarketCapWan: Double?,
    val quoteTime: String,
    val tradeDate: String,
    val source: String,
)
