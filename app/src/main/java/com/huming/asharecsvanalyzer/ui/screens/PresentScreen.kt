package com.huming.asharecsvanalyzer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huming.asharecsvanalyzer.UiState
import com.huming.asharecsvanalyzer.data.Formatters
import com.huming.asharecsvanalyzer.data.StockRow
import com.huming.asharecsvanalyzer.ui.components.HistogramChart
import com.huming.asharecsvanalyzer.ui.components.SummaryCard
import com.huming.asharecsvanalyzer.ui.theme.DownGreen
import com.huming.asharecsvanalyzer.ui.theme.UpRed

@Composable
fun PresentScreen(
    state: UiState,
    onBack: () -> Unit,
) {
    val a = state.analysis
    if (a == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("暂无分析结果，请先完成分析。")
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onBack) { Text("← 返回分析") }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "市场呈现",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = buildString {
                append("共 ${Formatters.number(a.stockCount)} 只")
                a.tradeDate?.let { append(" · $it") }
                state.sourceLabel?.let { append("\n来源：$it") }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard("上涨", Formatters.number(a.upCount), valueColor = UpRed, modifier = Modifier.weight(1f))
            SummaryCard("下跌", Formatters.number(a.downCount), valueColor = DownGreen, modifier = Modifier.weight(1f))
            SummaryCard("平盘", Formatters.number(a.flatCount), modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                "平均涨跌幅",
                Formatters.changePercent(a.avgChangePercent),
                valueColor = if (a.avgChangePercent >= 0) UpRed else DownGreen,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                "中位数",
                Formatters.changePercent(a.medianChangePercent),
                valueColor = if (a.medianChangePercent >= 0) UpRed else DownGreen,
                modifier = Modifier.weight(1f),
            )
        }

        SummaryCard(
            title = "总成交额",
            value = Formatters.turnover(a.totalTurnover),
            subtitle = "成交量 ${Formatters.volume(a.totalVolume)}",
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            HistogramChart(
                buckets = a.histogram,
                modifier = Modifier.padding(16.dp),
            )
        }

        RankedList("涨幅榜 TOP", a.topGainers) { Formatters.changePercent(it.changePercent) to UpRed }
        RankedList("跌幅榜 TOP", a.topLosers) {
            Formatters.changePercent(it.changePercent) to DownGreen
        }
        RankedList("成交额 TOP", a.topByTurnover) {
            Formatters.turnover(it.turnover) to Color.Unspecified
        }
        RankedList("换手率 TOP", a.topByTurnoverRatio) {
            Formatters.percent(it.turnoverRatio) to Color.Unspecified
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("← 返回分析")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RankedList(
    title: String,
    items: List<StockRow>,
    metric: (StockRow) -> Pair<String, Color>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            items.forEachIndexed { index, stock ->
                val (value, color) = metric(stock)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${index + 1}. ${stock.name}",
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = stock.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = value,
                        fontWeight = FontWeight.Bold,
                        color = if (color == Color.Unspecified)
                            MaterialTheme.colorScheme.onSurface else color,
                    )
                }
                if (index < items.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}
