package com.titanos.core

import com.titanos.domain.model.ModeType
import com.titanos.domain.model.SystemMetrics

interface AiOptimizationHooks {
    fun onMetricsUpdated(metrics: SystemMetrics)
    fun onModeChanged(mode: ModeType)
    fun predictBestMode(metrics: SystemMetrics): ModeType?
}

class NoOpAiOptimizationHooks : AiOptimizationHooks {
    override fun onMetricsUpdated(metrics: SystemMetrics) = Unit
    override fun onModeChanged(mode: ModeType) = Unit
    override fun predictBestMode(metrics: SystemMetrics): ModeType? = null
}
