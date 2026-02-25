package com.titanos.domain.repo

import com.titanos.domain.model.ModeType
import kotlinx.coroutines.flow.Flow

interface ModeRepository {
    val activeMode: Flow<ModeType>
    suspend fun setMode(mode: ModeType)
}
