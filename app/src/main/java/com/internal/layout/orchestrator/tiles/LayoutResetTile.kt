package com.internal.layout.orchestrator.tiles

import android.content.Intent
import android.service.quicksettings.TileService
import com.internal.layout.orchestrator.MainActivity

class LayoutResetTile : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("action", "reset_layout")
        }
        startActivityAndCollapse(intent)
    }
}
