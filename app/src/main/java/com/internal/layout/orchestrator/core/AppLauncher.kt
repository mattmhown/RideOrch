package com.internal.layout.orchestrator.core

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object AppLauncher {
    private const val TAG = "AppLauncher"

    fun launchAppInBounds(context: Context, packageName: String, bounds: Rect) {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                context.startActivity(launchIntent)
                Thread.sleep(1000)
            } else {
                Log.e(TAG, "No launch intent for $packageName")
                return
            }

            val taskId = getTaskIdForPackage(packageName)
            if (taskId != -1) {
                val command = "am task resize $taskId ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}"
                Log.d(TAG, "Executing: $command")
                executeShellCommand(command)
            } else {
                Log.e(TAG, "Could not find task ID for $packageName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error launching/resizing app $packageName", e)
        }
    }

    fun closeApp(packageName: String) {
        try {
            val command = "am force-stop $packageName"
            Log.d(TAG, "Executing: $command")
            executeShellCommand(command)
        } catch (e: Exception) {
            Log.e(TAG, "Error closing app $packageName", e)
        }
    }

    private fun getTaskIdForPackage(packageName: String): Int {
        try {
            val process = Runtime.getRuntime().exec("am stack list")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains(packageName)) {
                    val taskIdString = line?.substringAfter("taskId=")?.substringBefore(":")
                    if (taskIdString != null) {
                        return taskIdString.trim().toInt()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task ID for $packageName", e)
        }
        return -1
    }

    private fun executeShellCommand(command: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute shell command (maybe no root?): $command", e)
        }
    }
}
