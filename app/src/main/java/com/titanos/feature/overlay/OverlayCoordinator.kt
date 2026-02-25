package com.titanos.feature.overlay

import com.titanos.domain.model.ModeType
import com.titanos.domain.repo.ModeRepository
import com.titanos.domain.repo.SystemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OverlayCoordinator(
    private val modeRepository: ModeRepository,
    private val systemRepository: SystemRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        scope.launch {
            modeRepository.activeMode.collectLatest { mode ->
                val show = mode == ModeType.GameMode || mode == ModeType.SkyViewRadar
                systemRepository.toggleOverlays(show)
            }
        }
    }
}
