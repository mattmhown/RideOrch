package com.internal.layout.orchestrator

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.internal.layout.orchestrator.data.LayoutConfig
import com.internal.layout.orchestrator.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigBinding
    private lateinit var currentConfig: LayoutConfig
    private var appSelectionTarget: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadConfig()
        setupUI()
    }

    private fun loadConfig() {
        currentConfig = LayoutConfig.load(this)
        updateUI()
    }

    private fun setupUI() {
        binding.btnSelectTopApp.setOnClickListener { selectApp("top") }
        binding.btnSelectBottomLeftApp.setOnClickListener { selectApp("bottomLeft") }
        binding.btnSelectBottomRightApp.setOnClickListener { selectApp("bottomRight") }
        binding.btnSaveConfig.setOnClickListener { saveConfigAndFinish() }

        binding.seekBarTopHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentConfig = currentConfig.copy(topPaneHeightPercent = progress)
                    binding.tvTopHeight.text = "Top Height: $progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateUI() {
        binding.tvTopApp.text = "Top: ${getAppName(currentConfig.topAppPackage)}"
        binding.tvBottomLeftApp.text = "Bottom Left: ${getAppName(currentConfig.bottomLeftAppPackage)}"
        binding.tvBottomRightApp.text = "Bottom Right: ${getAppName(currentConfig.bottomRightAppPackage)}"
        binding.tvTopHeight.text = "Top Height: ${currentConfig.topPaneHeightPercent}%"
        binding.seekBarTopHeight.progress = currentConfig.topPaneHeightPercent
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName // Fallback to package name
        }
    }

    private fun selectApp(target: String) {
        appSelectionTarget = target
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val appList = pm.queryIntentActivities(mainIntent, 0)

        val appNames = appList.map { it.loadLabel(pm).toString() }.toTypedArray()
        val packageNames = appList.map { it.activityInfo.packageName }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select an App")
            .setItems(appNames) { _, which ->
                val selectedPackage = packageNames[which]
                onAppSelected(selectedPackage)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onAppSelected(packageName: String) {
        currentConfig = when (appSelectionTarget) {
            "top" -> currentConfig.copy(topAppPackage = packageName)
            "bottomLeft" -> currentConfig.copy(bottomLeftAppPackage = packageName)
            "bottomRight" -> currentConfig.copy(bottomRightAppPackage = packageName)
            else -> currentConfig
        }
        updateUI()
    }

    private fun saveConfigAndFinish() {
        LayoutConfig.save(this, currentConfig)
        Toast.makeText(this, "Configuration saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
