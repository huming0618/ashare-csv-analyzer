package com.huming.asharecsvanalyzer.data

import java.util.Locale
import kotlin.math.abs

object Formatters {

    fun changePercent(v: Double): String =
        String.format(Locale.CHINA, "%+.2f%%", v)

    fun percent(v: Double): String =
        String.format(Locale.CHINA, "%.2f%%", v)

    fun price(v: Double): String =
        String.format(Locale.CHINA, "%.3f", v)

    fun turnover(v: Double): String = when {
        abs(v) >= 1e12 -> String.format(Locale.CHINA, "%.2f万亿", v / 1e12)
        abs(v) >= 1e8 -> String.format(Locale.CHINA, "%.2f亿", v / 1e8)
        abs(v) >= 1e4 -> String.format(Locale.CHINA, "%.2f万", v / 1e4)
        else -> String.format(Locale.CHINA, "%.0f", v)
    }

    fun volume(v: Long): String = when {
        v >= 1e8 -> String.format(Locale.CHINA, "%.2f亿股", v / 1e8)
        v >= 1e4 -> String.format(Locale.CHINA, "%.2f万股", v / 1e4)
        else -> String.format(Locale.CHINA, "%d股", v)
    }

    fun number(v: Int): String = String.format(Locale.CHINA, "%,d", v)
}
