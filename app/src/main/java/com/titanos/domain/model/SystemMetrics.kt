package com.titanos.domain.model

data class SystemMetrics(
    val cpuPercent: Int,
    val gpuPercent: Int,
    val ramPercent: Int,
    val batteryPercent: Int,
    val networkKbps: Int,
    val temperatureC: Float,
    val fps: Int
)
