package com.titanos.feature.monitoring

import androidx.lifecycle.ViewModel
import com.titanos.core.AiOptimizationHooks
import com.titanos.core.NoOpAiOptimizationHooks
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.titanos.domain.model.ModeType
import com.titanos.domain.model.SystemMetrics
import com.titanos.domain.usecase.ObserveModeAndMetricsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MonitoringState(
    val mode: ModeType = ModeType.Normal,
    val metrics: SystemMetrics = SystemMetrics(0, 0, 0, 0, 0, 0f, 0),
    val thermalAlert: Boolean = false,
    val batteryDrainAlert: Boolean = false
)

class MonitoringViewModel(
    observeModeAndMetricsUseCase: ObserveModeAndMetricsUseCase,
    private val aiHooks: AiOptimizationHooks = NoOpAiOptimizationHooks()
) : ViewModel() {
    val state: StateFlow<MonitoringState> = observeModeAndMetricsUseCase()
        .map { (mode, metrics) ->
            aiHooks.onMetricsUpdated(metrics)
            aiHooks.onModeChanged(mode)
            MonitoringState(
                mode = mode,
                metrics = metrics,
                thermalAlert = metrics.temperatureC > 42f,
                batteryDrainAlert = metrics.batteryPercent < 15 && metrics.cpuPercent > 70
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MonitoringState()
        )

    companion object {
        fun factory(useCase: ObserveModeAndMetricsUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MonitoringViewModel(useCase) as T
                }
            }
    }
}
