package com.titanos.core

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Immutable

@Immutable
data class ThemeProfile(
    val packageName: String,
    val displayName: String,
    val accentHex: String = "#1C4E8A",
    val fontFamily: String = "System"
)

class ThemeEngine(
    private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager

    fun discoverIconPacks(): List<ThemeProfile> {
        val intent = android.content.Intent("org.adw.launcher.THEMES")
        return packageManager.queryIntentActivities(intent, 0).map {
            ThemeProfile(
                packageName = it.activityInfo.packageName,
                displayName = it.loadLabel(packageManager).toString()
            )
        }
    }

    fun applyTheme(profile: ThemeProfile) {
        // Hook point for dynamic resource overlays / icon pack parsing.
        // Intentionally lightweight in scaffold for ROM-level future integration.
    }
}
