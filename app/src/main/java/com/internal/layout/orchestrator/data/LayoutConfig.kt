package com.internal.layout.orchestrator.data

import android.content.Context
import com.google.gson.Gson
import java.io.File

data class LayoutConfig(
    val topAppPackage: String = "com.android.chrome",
    val bottomLeftAppPackage: String = "com.google.android.apps.messaging",
    val bottomRightAppPackage: String = "com.google.android.youtube",
    val topPaneHeightPercent: Int = 33,
    val autoLaunchOnBoot: Boolean = true
) {
    companion object {
        private const val CONFIG_FILE = "layout_config.json"
        private val gson = Gson()

        fun save(context: Context, config: LayoutConfig) {
            try {
                val file = File(context.filesDir, CONFIG_FILE)
                file.writeText(gson.toJson(config))
            } catch (e: Exception) {
                // Handle exception
            }
        }

        fun load(context: Context): LayoutConfig {
            return try {
                val file = File(context.filesDir, CONFIG_FILE)
                if (file.exists()) {
                    gson.fromJson(file.readText(), LayoutConfig::class.java)
                } else {
                    LayoutConfig() // Return default config
                }
            } catch (e: Exception) {
                LayoutConfig() // Return default on error
            }
        }
    }
}
