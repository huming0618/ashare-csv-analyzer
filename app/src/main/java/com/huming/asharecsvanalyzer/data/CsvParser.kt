package com.huming.asharecsvanalyzer.data

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

object CsvParser {

    fun parse(inputStream: InputStream): List<StockRow> {
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        val rows = mutableListOf<StockRow>()
        var headerSkipped = false

        reader.useLines { lines ->
            for (raw in lines) {
                var line = raw
                if (!headerSkipped) {
                    // Strip UTF-8 BOM if present
                    if (line.isNotEmpty() && line[0] == '\uFEFF') {
                        line = line.substring(1)
                    }
                    headerSkipped = true
                    continue
                }
                if (line.isBlank()) continue
                val cols = parseCsvLine(line)
                if (cols.size < 13) continue
                try {
                    rows += StockRow(
                        fullCode = cols.getOrElse(0) { "" }.trim(),
                        code = cols.getOrElse(1) { "" }.trim(),
                        name = cols.getOrElse(2) { "" }.trim(),
                        lastPrice = cols.getOrElse(3) { "0" }.toDoubleOrZero(),
                        changeAmount = cols.getOrElse(4) { "0" }.toDoubleOrZero(),
                        changePercent = cols.getOrElse(5) { "0" }.toDoubleOrZero(),
                        open = cols.getOrElse(6) { "0" }.toDoubleOrZero(),
                        high = cols.getOrElse(7) { "0" }.toDoubleOrZero(),
                        low = cols.getOrElse(8) { "0" }.toDoubleOrZero(),
                        prevClose = cols.getOrElse(9) { "0" }.toDoubleOrZero(),
                        volume = cols.getOrElse(10) { "0" }.toLongOrZero(),
                        turnover = cols.getOrElse(11) { "0" }.toDoubleOrZero(),
                        turnoverRatio = cols.getOrElse(12) { "0" }.toDoubleOrZero(),
                        pe = cols.getOrNull(13)?.toDoubleOrNull(),
                        pb = cols.getOrNull(14)?.toDoubleOrNull(),
                        totalMarketCapWan = cols.getOrNull(15)?.toDoubleOrNull(),
                        floatMarketCapWan = cols.getOrNull(16)?.toDoubleOrNull(),
                        quoteTime = cols.getOrElse(17) { "" }.trim(),
                        tradeDate = cols.getOrElse(18) { "" }.trim(),
                        source = cols.getOrElse(19) { "" }.trim(),
                    )
                } catch (_: Exception) {
                    // skip malformed rows
                }
            }
        }
        return rows
    }

    /** Simple CSV splitter that respects quoted fields. */
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    result += sb.toString()
                    sb.clear()
                }
                else -> sb.append(c)
            }
            i++
        }
        result += sb.toString()
        return result
    }

    private fun String.toDoubleOrZero(): Double =
        trim().replace(",", "").toDoubleOrNull() ?: 0.0

    private fun String.toLongOrZero(): Long =
        trim().replace(",", "").toDoubleOrNull()?.toLong() ?: 0L

    private fun String.toDoubleOrNull(): Double? =
        trim().replace(",", "").toDoubleOrNull()
}
