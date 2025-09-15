package com.internal.layout.orchestrator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internal.layout.orchestrator.core.LayoutOrchestrator
import com.internal.layout.orchestrator.data.LayoutConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrchestratorViewModel : ViewModel() {
    suspend fun applyLayout(orchestrator: LayoutOrchestrator, config: LayoutConfig): Boolean =
        withContext(Dispatchers.IO) {
            orchestrator.applyLayout(config)
        }

    fun resetLayout(orchestrator: LayoutOrchestrator) {
        viewModelScope.launch(Dispatchers.IO) {
            orchestrator.resetLayout()
        }
    }
}
