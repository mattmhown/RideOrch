package com.internal.layout.orchestrator.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.internal.layout.orchestrator.core.LayoutOrchestrator
import com.internal.layout.orchestrator.data.LayoutConfig
import com.internal.layout.orchestrator.services.LayoutWatchdogService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val config = LayoutConfig.load(context)
            if (config.autoLaunchOnBoot) {
                CoroutineScope(Dispatchers.IO).launch {
                    LayoutOrchestrator(context).applyLayout(config)
                    LayoutWatchdogService.start(context)
                }
            }
        }
    }
}
