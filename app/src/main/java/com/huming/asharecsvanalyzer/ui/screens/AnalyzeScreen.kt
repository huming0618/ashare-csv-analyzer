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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huming.asharecsvanalyzer.UiState
import com.huming.asharecsvanalyzer.data.Formatters
import com.huming.asharecsvanalyzer.ui.components.SummaryCard
import com.huming.asharecsvanalyzer.ui.theme.DownGreen
import com.huming.asharecsvanalyzer.ui.theme.UpRed

@Composable
fun AnalyzeScreen(
    state: UiState,
    onAnalyze: () -> Unit,
    onPresent: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "分析行情数据",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "已载入 ${Formatters.number(state.stocks.size)} 只股票。点击下方按钮计算涨跌统计、成交额与排行。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(
            onClick = onAnalyze,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.stocks.isNotEmpty(),
        ) {
            Text(if (state.analysis == null) "开始分析" else "重新分析")
        }

        if (state.isLoading) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("分析中…")
            }
        }

        state.analysis?.let { a ->
            Text("分析结果摘要", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SummaryCard(
                    title = "上涨",
                    value = Formatters.number(a.upCount),
                    valueColor = UpRed,
                    modifier = Modifier.weight(1f),
                )
                SummaryCard(
                    title = "下跌",
                    value = Formatters.number(a.downCount),
                    valueColor = DownGreen,
                    modifier = Modifier.weight(1f),
                )
                SummaryCard(
                    title = "平盘",
                    value = Formatters.number(a.flatCount),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SummaryCard(
                    title = "平均涨跌幅",
                    value = Formatters.changePercent(a.avgChangePercent),
                    valueColor = if (a.avgChangePercent >= 0) UpRed else DownGreen,
                    modifier = Modifier.weight(1f),
                )
                SummaryCard(
                    title = "中位数涨跌幅",
                    value = Formatters.changePercent(a.medianChangePercent),
                    valueColor = if (a.medianChangePercent >= 0) UpRed else DownGreen,
                    modifier = Modifier.weight(1f),
                )
            }

            SummaryCard(
                title = "总成交额",
                value = Formatters.turnover(a.totalTurnover),
                subtitle = "成交量 ${Formatters.volume(a.totalVolume)}" +
                    (a.tradeDate?.let { " · $it" } ?: ""),
            )

            Button(onClick = onPresent, modifier = Modifier.fillMaxWidth()) {
                Text("下一步：呈现 →")
            }
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("← 返回导入")
        }
    }
}
