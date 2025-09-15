package com.internal.layout.orchestrator.layout

import android.content.Context
import android.graphics.Rect
import android.view.WindowManager
import android.os.Build
import android.view.WindowMetrics

object LayoutCalculator {

    fun getThreePaneBounds(context: Context, topPaneHeightPercent: Int): Triple<Rect, Rect, Rect> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val screenWidth: Int
        val screenHeight: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            screenWidth = bounds.width()
            screenHeight = bounds.height()
        } else {
            val displayMetrics = android.util.DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }

        val topHeight = (screenHeight * (topPaneHeightPercent / 100.0)).toInt()
        val bottomHeight = screenHeight - topHeight

        val topPane = Rect(0, 0, screenWidth, topHeight)
        val bottomLeftPane = Rect(0, topHeight, screenWidth / 2, screenHeight)
        val bottomRightPane = Rect(screenWidth / 2, topHeight, screenWidth, screenHeight)

        return Triple(topPane, bottomLeftPane, bottomRightPane)
    }
}
