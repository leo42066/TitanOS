package com.titanos.domain.repo

import com.titanos.domain.model.AppInfo
import com.titanos.domain.model.SystemMetrics
import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    fun installedApps(): List<AppInfo>
    fun watchMetrics(): Flow<SystemMetrics>
    suspend fun applyPerformanceProfile(enabled: Boolean)
    suspend fun suppressBackgroundTasks(enabled: Boolean)
    suspend fun toggleOverlays(enabled: Boolean)
}
