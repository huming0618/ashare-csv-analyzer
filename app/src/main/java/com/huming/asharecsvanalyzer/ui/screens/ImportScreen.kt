package com.huming.asharecsvanalyzer.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huming.asharecsvanalyzer.BuildConfig
import com.huming.asharecsvanalyzer.UiState
import com.huming.asharecsvanalyzer.data.Formatters

@Composable
fun ImportScreen(
    state: UiState,
    onPickFile: (android.net.Uri) -> Unit,
    onLoadSample: () -> Unit,
    onContinue: () -> Unit,
) {
    // GetContent is more reliable than OpenDocument on many OEMs for one-shot reads.
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) onPickFile(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "导入 A 股日行情 CSV",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "版本 ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) · 选择 fetcher 导出的 CSV，或加载内置示例。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(
            onClick = { picker.launch("*/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            Icon(Icons.Default.FolderOpen, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("从文件选择 CSV")
        }

        OutlinedButton(
            onClick = onLoadSample,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            Icon(Icons.Default.Science, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("加载内置示例数据")
        }

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("正在读取…")
            }
        }

        if (state.stocks.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("已导入", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = state.sourceLabel ?: "CSV",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        text = "共 ${Formatters.number(state.stocks.size)} 只股票" +
                            (state.stocks.firstOrNull()?.tradeDate?.let { " · 交易日 $it" } ?: ""),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("下一步：分析")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("CSV 列要求", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "代码全称,代码,名称,最新价,涨跌额,涨跌幅%,今开,最高,最低,昨收," +
                        "成交量(股),成交额,换手率%,市盈率,市净率,总市值(万),流通市值(万)," +
                        "行情时间,交易日期,数据来源",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
