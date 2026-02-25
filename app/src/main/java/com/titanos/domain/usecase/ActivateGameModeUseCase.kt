package com.titanos.domain.usecase

import com.titanos.domain.model.ModeType
import com.titanos.domain.repo.ModeRepository
import com.titanos.domain.repo.SystemRepository

class ActivateGameModeUseCase(
    private val modeRepository: ModeRepository,
    private val systemRepository: SystemRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        if (enabled) {
            modeRepository.setMode(ModeType.GameMode)
            systemRepository.applyPerformanceProfile(true)
            systemRepository.suppressBackgroundTasks(true)
            systemRepository.toggleOverlays(true)
        } else {
            modeRepository.setMode(ModeType.Normal)
            systemRepository.applyPerformanceProfile(false)
            systemRepository.suppressBackgroundTasks(false)
            systemRepository.toggleOverlays(false)
        }
    }
}
