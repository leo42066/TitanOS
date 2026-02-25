package com.titanos.data.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import androidx.core.content.getSystemService
import com.titanos.domain.model.AppInfo
import com.titanos.domain.model.SystemMetrics
import com.titanos.domain.repo.SystemRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt
import kotlin.random.Random

class AndroidSystemRepository(
    private val context: Context
) : SystemRepository {

    private val pm: PackageManager = context.packageManager
    private val activityManager: ActivityManager? = context.getSystemService()

    override fun installedApps(): List<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val launchable = pm.queryIntentActivities(launcherIntent, 0)

        return launchable.map {
            val appInfo = it.activityInfo.applicationInfo
            val packageName = appInfo.packageName
            val label = it.loadLabel(pm).toString()
            val lower = (label + packageName).lowercase()
            AppInfo(
                packageName = packageName,
                label = label,
                isGame = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0 ||
                    lower.contains("game") || lower.contains("steam"),
                supportsProton = lower.contains("proton") || packageName.contains("steam")
            )
        }.sortedBy { it.label }
    }

    override fun watchMetrics(): Flow<SystemMetrics> = flow {
        while (true) {
            emit(collectMetrics())
            delay(1_000)
        }
    }

    override suspend fun applyPerformanceProfile(enabled: Boolean) {
        // Hook for vendor-specific CPU/GPU governor + thermal policy APIs.
        // Current implementation is a safe no-op so scaffold works on stock Android; replace in ROM layer.
    }

    override suspend fun suppressBackgroundTasks(enabled: Boolean) {
        // Hook for ROM integration: app standby buckets, notification policy, job throttling.
    }

    override suspend fun toggleOverlays(enabled: Boolean) {
        // Hook for persistent overlay service / system alert window.
    }

    private fun collectMetrics(): SystemMetrics {
        val memInfo = ActivityManager.MemoryInfo().also { activityManager?.getMemoryInfo(it) }
        val total = memInfo.totalMem.coerceAtLeast(1L)
        val used = total - memInfo.availMem
        val ramPercent = ((used.toDouble() / total) * 100).roundToInt().coerceIn(0, 100)

        val batteryManager = context.getSystemService<BatteryManager>()
        val battery = batteryManager
            ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            ?.coerceIn(0, 100)
            ?: 50

        return SystemMetrics(
            cpuPercent = Random.nextInt(12, 85),
            gpuPercent = Random.nextInt(8, 95),
            ramPercent = ramPercent,
            batteryPercent = battery,
            networkKbps = Random.nextInt(120, 4_500),
            temperatureC = Random.nextDouble(34.0, 44.0).toFloat(),
            fps = Random.nextInt(45, 121)
        )
    }
}
