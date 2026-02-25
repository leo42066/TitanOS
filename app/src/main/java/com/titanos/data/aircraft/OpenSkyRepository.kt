package com.titanos.data.aircraft

import com.titanos.domain.model.Aircraft
import com.titanos.domain.repo.AircraftRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class OpenSkyRepository(
    private val api: OpenSkyApi
) : AircraftRepository {
    override fun aircraftFeed(latitude: Double, longitude: Double): Flow<List<Aircraft>> = flow {
        while (true) {
            val response = api.fetchNearbyAircraft(
                latMin = latitude - 0.6,
                lonMin = longitude - 0.6,
                latMax = latitude + 0.6,
                lonMax = longitude + 0.6
            )
            val mapped = response.states.orEmpty().mapNotNull(::toAircraft)
            emit(mapped)
            delay(5_000)
        }
    }

    private fun toAircraft(item: JsonArray): Aircraft? {
        val id = item.getOrNull(0)?.jsonPrimitive?.contentOrNull ?: return null
        val callsign = item.getOrNull(1)?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        val origin = item.getOrNull(2)?.jsonPrimitive?.contentOrNull
        val lon = item.getOrNull(5)?.jsonPrimitive?.doubleOrNull ?: return null
        val lat = item.getOrNull(6)?.jsonPrimitive?.doubleOrNull ?: return null
        val altitude = item.getOrNull(7)?.jsonPrimitive?.floatOrNull ?: 0f
        val speed = item.getOrNull(9)?.jsonPrimitive?.floatOrNull ?: 0f
        val heading = item.getOrNull(10)?.jsonPrimitive?.floatOrNull ?: 0f
        val destination = item.getOrNull(15)?.jsonPrimitive?.contentOrNull

        return Aircraft(
            id = id,
            callsign = callsign,
            latitude = lat,
            longitude = lon,
            heading = heading,
            altitudeMeters = altitude,
            speedMps = speed,
            origin = origin,
            destination = destination
        )
    }
}
