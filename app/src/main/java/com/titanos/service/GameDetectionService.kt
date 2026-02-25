package com.titanos.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.titanos.TitanApplication
import com.titanos.domain.model.ModeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Detects foreground app transitions and toggles GameMode for Steam Link or likely Proton/game apps.
 * Uses Accessibility for broad compatibility in scaffold form; ROM builds can swap for UsageStats observer.
 */
class GameDetectionService : AccessibilityService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        val isSteamOrGame = packageName.contains("steam") || packageName.contains("proton") || packageName.contains("game")

        val modeRepository = (application as TitanApplication).container.modeRepository
        scope.launch {
            modeRepository.setMode(if (isSteamOrGame) ModeType.GameMode else ModeType.Normal)
        }
    }

    override fun onInterrupt() = Unit
}
