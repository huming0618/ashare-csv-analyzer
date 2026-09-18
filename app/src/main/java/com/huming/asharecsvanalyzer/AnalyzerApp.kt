package com.huming.asharecsvanalyzer

import android.app.Application
import java.io.File

class AnalyzerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            try {
                File(filesDir, "last_crash.txt").writeText(
                    buildString {
                        appendLine(System.currentTimeMillis().toString())
                        appendLine(thread.name)
                        appendLine(error.stackTraceToString())
                    },
                )
            } catch (_: Exception) {
                // ignore crash-log failures
            }
            previous?.uncaughtException(thread, error)
        }
    }
}
