package com.titanos.domain.model

data class Aircraft(
    val id: String,
    val callsign: String,
    val latitude: Double,
    val longitude: Double,
    val heading: Float,
    val altitudeMeters: Float,
    val speedMps: Float,
    val origin: String?,
    val destination: String?
)
