package com.internal.layout.orchestrator.services

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.internal.layout.orchestrator.data.LayoutConfig

class WindowBoundsService : AccessibilityService() {
    private val TAG = "WindowBoundsService"
    private lateinit var config: LayoutConfig

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Accessibility Service connected.")
        config = LayoutConfig.load(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // This service is primarily for enabling freeform window management.
        // The actual monitoring can be complex and lead to unwanted loops.
        // For now, its main purpose is to be enabled to satisfy system checks.
    }

    override fun onInterrupt() {
        Log.w(TAG, "Accessibility Service interrupted.")
    }
}
