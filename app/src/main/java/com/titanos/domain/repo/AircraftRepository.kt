package com.titanos.domain.repo

import com.titanos.domain.model.Aircraft
import kotlinx.coroutines.flow.Flow

interface AircraftRepository {
    fun aircraftFeed(latitude: Double, longitude: Double): Flow<List<Aircraft>>
}
