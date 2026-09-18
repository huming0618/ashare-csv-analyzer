package com.huming.asharecsvanalyzer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huming.asharecsvanalyzer.data.HistogramBucket
import com.huming.asharecsvanalyzer.ui.theme.DownGreen
import com.huming.asharecsvanalyzer.ui.theme.FlatGray
import com.huming.asharecsvanalyzer.ui.theme.UpRed

@Composable
fun HistogramChart(
    buckets: List<HistogramBucket>,
    modifier: Modifier = Modifier,
) {
    if (buckets.isEmpty()) return
    val maxCount = buckets.maxOf { it.count }.coerceAtLeast(1)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val density = LocalDensity.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "涨跌幅分布",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 4.dp),
        ) {
            val labelH = with(density) { 28.dp.toPx() }
            val countH = with(density) { 16.dp.toPx() }
            val chartTop = countH + 4f
            val chartBottom = size.height - labelH
            val chartH = (chartBottom - chartTop).coerceAtLeast(1f)
            val gap = size.width * 0.02f
            val barW = (size.width - gap * (buckets.size + 1)) / buckets.size

            buckets.forEachIndexed { i, b ->
                val barH = (b.count.toFloat() / maxCount) * chartH
                val left = gap + i * (barW + gap)
                val top = chartBottom - barH
                val barColor = when (i) {
                    0, 1, 2 -> DownGreen
                    3 -> FlatGray
                    else -> UpRed
                }
                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barW, barH),
                )
                // count label
                drawContext.canvas.nativeCanvas.drawText(
                    b.count.toString(),
                    left + barW / 2f,
                    top - 4f,
                    android.graphics.Paint().apply {
                        color = labelColor
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = with(density) { 11.sp.toPx() }
                        isAntiAlias = true
                    },
                )
                // x label
                drawContext.canvas.nativeCanvas.drawText(
                    b.label,
                    left + barW / 2f,
                    size.height - 4f,
                    android.graphics.Paint().apply {
                        color = labelColor
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = with(density) { 10.sp.toPx() }
                        isAntiAlias = true
                    },
                )
            }
        }
    }
}
