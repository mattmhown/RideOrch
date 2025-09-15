package com.internal.layout.orchestrator.core

import android.content.Context
import android.util.Log
import com.internal.layout.orchestrator.data.LayoutConfig
import com.internal.layout.orchestrator.layout.LayoutCalculator

class LayoutOrchestrator(private val context: Context) {
    private val TAG = "LayoutOrchestrator"

    fun applyLayout(config: LayoutConfig): Boolean {
        return try {
            Log.d(TAG, "Applying 3-pane layout")
            val (topBounds, blBounds, brBounds) = LayoutCalculator.getThreePaneBounds(
                context,
                config.topPaneHeightPercent
            )

            // Launch apps in their designated bounds
            AppLauncher.launchAppInBounds(context, config.bottomRightAppPackage, brBounds)
            Thread.sleep(250)
            AppLauncher.launchAppInBounds(context, config.bottomLeftAppPackage, blBounds)
            Thread.sleep(250)
            AppLauncher.launchAppInBounds(context, config.topAppPackage, topBounds)

            Log.d(TAG, "Layout applied successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply layout", e)
            false
        }
    }

    fun resetLayout() {
        Log.d(TAG, "Resetting layout")
        val config = LayoutConfig.load(context)
        AppLauncher.closeApp(config.topAppPackage)
        AppLauncher.closeApp(config.bottomLeftAppPackage)
        AppLauncher.closeApp(config.bottomRightAppPackage)
    }
}
