package com.huming.asharecsvanalyzer

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huming.asharecsvanalyzer.data.CsvParser
import com.huming.asharecsvanalyzer.data.MarketAnalysis
import com.huming.asharecsvanalyzer.data.MarketAnalyzer
import com.huming.asharecsvanalyzer.data.StockRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class AppStep { Import, Analyze, Present }

data class UiState(
    val step: AppStep = AppStep.Import,
    val sourceLabel: String? = null,
    val stocks: List<StockRow> = emptyList(),
    val analysis: MarketAnalysis? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun goTo(step: AppStep) {
        val s = _ui.value
        when (step) {
            AppStep.Import -> _ui.update { it.copy(step = AppStep.Import, error = null) }
            AppStep.Analyze -> {
                if (s.stocks.isEmpty()) {
                    _ui.update { it.copy(error = "请先导入 CSV 文件") }
                } else {
                    _ui.update { it.copy(step = AppStep.Analyze, error = null) }
                }
            }
            AppStep.Present -> {
                if (s.analysis == null) {
                    _ui.update { it.copy(error = "请先完成分析") }
                } else {
                    _ui.update { it.copy(step = AppStep.Present, error = null) }
                }
            }
        }
    }

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }

    fun loadFromUri(uri: Uri, label: String? = null) {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                val stocks = withContext(Dispatchers.IO) {
                    val app = getApplication<Application>()
                    val cache = File(app.cacheDir, "import_${System.currentTimeMillis()}.csv")
                    try {
                        // Prefer a one-shot byte copy: avoids OEM URI permission races
                        // after the document picker returns and the activity resumes.
                        app.contentResolver.openInputStream(uri)?.use { input ->
                            cache.outputStream().use { output -> input.copyTo(output) }
                        } ?: throw IllegalStateException("无法打开所选文件（可能没有读取权限）")

                        if (!cache.exists() || cache.length() == 0L) {
                            throw IllegalStateException("所选文件为空")
                        }
                        CsvParser.parseFile(cache)
                    } finally {
                        runCatching { cache.delete() }
                    }
                }
                if (stocks.isEmpty()) {
                    throw IllegalStateException("未解析到有效股票数据，请检查 CSV 格式与编码（UTF-8 / GBK）")
                }
                _ui.update {
                    it.copy(
                        isLoading = false,
                        stocks = stocks,
                        sourceLabel = label ?: uri.lastPathSegment ?: "已选文件",
                        analysis = null,
                        step = AppStep.Import,
                    )
                }
            } catch (e: Throwable) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                        error = (e.message ?: "导入失败") + " (${e.javaClass.simpleName})",
                    )
                }
            }
        }
    }

    fun loadSampleAsset() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                val stocks = withContext(Dispatchers.IO) {
                    getApplication<Application>().assets
                        .open("sample_a_share_spot.csv")
                        .use { CsvParser.parse(it) }
                }
                if (stocks.isEmpty()) {
                    throw IllegalStateException("示例数据为空")
                }
                _ui.update {
                    it.copy(
                        isLoading = false,
                        stocks = stocks,
                        sourceLabel = "内置示例 sample_a_share_spot.csv",
                        analysis = null,
                        step = AppStep.Import,
                    )
                }
            } catch (e: Throwable) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                        error = (e.message ?: "加载示例失败") + " (${e.javaClass.simpleName})",
                    )
                }
            }
        }
    }

    fun runAnalysis() {
        val stocks = _ui.value.stocks
        if (stocks.isEmpty()) {
            _ui.update { it.copy(error = "请先导入 CSV 文件") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null, step = AppStep.Analyze) }
            try {
                val analysis = withContext(Dispatchers.Default) {
                    MarketAnalyzer.analyze(stocks)
                }
                _ui.update {
                    it.copy(isLoading = false, analysis = analysis, step = AppStep.Analyze)
                }
            } catch (e: Throwable) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                        error = (e.message ?: "分析失败") + " (${e.javaClass.simpleName})",
                    )
                }
            }
        }
    }

    fun goPresent() {
        if (_ui.value.analysis == null) {
            _ui.update { it.copy(error = "请先完成分析") }
            return
        }
        _ui.update { it.copy(step = AppStep.Present, error = null) }
    }
}
