package com.huming.asharecsvanalyzer.data

object MarketAnalyzer {

    private val HISTOGRAM_BOUNDS = listOf(
        Double.NEGATIVE_INFINITY to -5.0,
        -5.0 to -3.0,
        -3.0 to -1.0,
        -1.0 to 0.0,
        0.0 to 1.0,
        1.0 to 3.0,
        3.0 to 5.0,
        5.0 to Double.POSITIVE_INFINITY,
    )

    private val HISTOGRAM_LABELS = listOf(
        "≤-5%", "-5~-3%", "-3~-1%", "-1~0%",
        "0~1%", "1~3%", "3~5%", "≥5%",
    )

    fun analyze(stocks: List<StockRow>, topN: Int = 10): MarketAnalysis {
        if (stocks.isEmpty()) {
            return MarketAnalysis(
                stockCount = 0,
                tradeDate = null,
                upCount = 0,
                downCount = 0,
                flatCount = 0,
                avgChangePercent = 0.0,
                medianChangePercent = 0.0,
                totalTurnover = 0.0,
                totalVolume = 0L,
                topGainers = emptyList(),
                topLosers = emptyList(),
                topByTurnover = emptyList(),
                topByTurnoverRatio = emptyList(),
                histogram = emptyList(),
            )
        }

        var up = 0
        var down = 0
        var flat = 0
        var sumPct = 0.0
        var totalTurnover = 0.0
        var totalVolume = 0L
        val pcts = ArrayList<Double>(stocks.size)

        for (s in stocks) {
            when {
                s.changePercent > 0.0 -> up++
                s.changePercent < 0.0 -> down++
                else -> flat++
            }
            sumPct += s.changePercent
            pcts += s.changePercent
            totalTurnover += s.turnover
            totalVolume += s.volume
        }

        pcts.sort()
        val median = if (pcts.size % 2 == 0) {
            (pcts[pcts.size / 2 - 1] + pcts[pcts.size / 2]) / 2.0
        } else {
            pcts[pcts.size / 2]
        }

        val histogram = HISTOGRAM_BOUNDS.mapIndexed { idx, (lo, hi) ->
            val adjustedCount = when (idx) {
                0 -> stocks.count { it.changePercent <= -5.0 }
                HISTOGRAM_BOUNDS.lastIndex -> stocks.count { it.changePercent >= 5.0 }
                else -> stocks.count { it.changePercent >= lo && it.changePercent < hi }
            }
            HistogramBucket(
                label = HISTOGRAM_LABELS[idx],
                minInclusive = if (lo == Double.NEGATIVE_INFINITY) Double.NEGATIVE_INFINITY else lo,
                maxExclusive = if (hi == Double.POSITIVE_INFINITY) Double.POSITIVE_INFINITY else hi,
                count = adjustedCount,
            )
        }

        return MarketAnalysis(
            stockCount = stocks.size,
            tradeDate = stocks.firstOrNull()?.tradeDate?.takeIf { it.isNotBlank() },
            upCount = up,
            downCount = down,
            flatCount = flat,
            avgChangePercent = sumPct / stocks.size,
            medianChangePercent = median,
            totalTurnover = totalTurnover,
            totalVolume = totalVolume,
            topGainers = stocks.sortedByDescending { it.changePercent }.take(topN),
            topLosers = stocks.sortedBy { it.changePercent }.take(topN),
            topByTurnover = stocks.sortedByDescending { it.turnover }.take(topN),
            topByTurnoverRatio = stocks.sortedByDescending { it.turnoverRatio }.take(topN),
            histogram = histogram,
        )
    }
}
