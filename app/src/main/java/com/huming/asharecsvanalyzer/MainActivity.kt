package com.huming.asharecsvanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.huming.asharecsvanalyzer.ui.components.StepHeader
import com.huming.asharecsvanalyzer.ui.screens.AnalyzeScreen
import com.huming.asharecsvanalyzer.ui.screens.ImportScreen
import com.huming.asharecsvanalyzer.ui.screens.PresentScreen
import com.huming.asharecsvanalyzer.ui.theme.AShareTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AShareTheme {
                val state by viewModel.ui.collectAsState()
                val snackbar = remember { SnackbarHostState() }

                LaunchedEffect(state.error) {
                    state.error?.let {
                        snackbar.showSnackbar(it)
                        viewModel.clearError()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("A股行情分析", fontWeight = FontWeight.Bold)
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbar) },
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        StepHeader(
                            current = state.step,
                            onStepClick = { viewModel.goTo(it) },
                        )
                        HorizontalDivider()
                        when (state.step) {
                            AppStep.Import -> ImportScreen(
                                state = state,
                                onPickFile = { uri -> viewModel.loadFromUri(uri) },
                                onLoadSample = { viewModel.loadSampleAsset() },
                                onContinue = { viewModel.runAnalysis() },
                            )
                            AppStep.Analyze -> AnalyzeScreen(
                                state = state,
                                onAnalyze = { viewModel.runAnalysis() },
                                onPresent = { viewModel.goPresent() },
                                onBack = { viewModel.goTo(AppStep.Import) },
                            )
                            AppStep.Present -> PresentScreen(
                                state = state,
                                onBack = { viewModel.goTo(AppStep.Analyze) },
                            )
                        }
                    }
                }
            }
        }
    }
}
