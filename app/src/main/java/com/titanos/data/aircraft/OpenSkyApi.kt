package com.titanos.data.aircraft

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenSkyApi {
    @GET("states/all")
    suspend fun fetchNearbyAircraft(
        @Query("lamin") latMin: Double,
        @Query("lomin") lonMin: Double,
        @Query("lamax") latMax: Double,
        @Query("lomax") lonMax: Double
    ): OpenSkyResponse
}

@Serializable
data class OpenSkyResponse(
    @SerialName("states") val states: List<JsonArray>? = null
)
