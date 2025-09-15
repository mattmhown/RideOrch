package com.internal.layout.orchestrator

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.internal.layout.orchestrator.core.LayoutOrchestrator
import com.internal.layout.orchestrator.data.LayoutConfig
import com.internal.layout.orchestrator.databinding.ActivityMainBinding
import com.internal.layout.orchestrator.services.LayoutWatchdogService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: OrchestratorViewModel by viewModels()
    private lateinit var orchestrator: LayoutOrchestrator
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orchestrator = LayoutOrchestrator(this)
        setupUI()
        checkPermissions()
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Update switch state in case the service was stopped from notifications
        binding.switchWatchdog.isChecked = LayoutWatchdogService.isRunning(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.getStringExtra("action") == "reset_layout") {
            viewModel.resetLayout(orchestrator)
            showToast("Layout reset")
        }
    }

    private fun setupUI() {
        binding.btnApplyLayout.setOnClickListener {
            lifecycleScope.launch {
                applyLayout()
            }
        }
        binding.btnConfigureApps.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }

        binding.switchWatchdog.isChecked = LayoutWatchdogService.isRunning(this)
        binding.switchWatchdog.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LayoutWatchdogService.start(this)
                showToast("Watchdog service started")
            } else {
                LayoutWatchdogService.stop(this)
                showToast("Watchdog service stopped")
            }
        }
    }

    private suspend fun applyLayout() {
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
            return
        }

        val config = LayoutConfig.load(this@MainActivity)
        val success = viewModel.applyLayout(orchestrator, config)
        showToast(if (success) "Layout applied" else "Apply failed. Check app permissions.")
        if (success && binding.switchWatchdog.isChecked) {
            LayoutWatchdogService.start(this@MainActivity)
        }
    }

    private fun checkPermissions() {
        if (!hasUsageStatsPermission()) {
            showToast("Please grant Usage Access permission")
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }
        if (!isIgnoringBatteryOptimizations()) {
            requestBatteryOptimizationExemption()
        }
        if (!isAccessibilityEnabled()) {
            showToast("Please enable the 'Layout Bounds Monitor' service")
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        showToast("Please grant 'Display over other apps' permission")
        startActivity(intent)
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun requestBatteryOptimizationExemption() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))
        showToast("Please disable battery optimization for this app")
        startActivity(intent)
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(packageName) == true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
