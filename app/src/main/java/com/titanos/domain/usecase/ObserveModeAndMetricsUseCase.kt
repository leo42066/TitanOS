package com.titanos.domain.usecase

import com.titanos.domain.model.ModeType
import com.titanos.domain.model.SystemMetrics
import com.titanos.domain.repo.ModeRepository
import com.titanos.domain.repo.SystemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveModeAndMetricsUseCase(
    private val modeRepository: ModeRepository,
    private val systemRepository: SystemRepository
) {
    operator fun invoke(): Flow<Pair<ModeType, SystemMetrics>> =
        modeRepository.activeMode.combine(systemRepository.watchMetrics()) { mode, metrics ->
            mode to metrics
        }
}
