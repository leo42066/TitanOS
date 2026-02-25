package com.titanos.core.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.titanos.data.aircraft.OpenSkyApi
import com.titanos.data.aircraft.OpenSkyRepository
import com.titanos.data.system.AndroidSystemRepository
import com.titanos.data.system.ModeDataStoreRepository
import com.titanos.domain.repo.AircraftRepository
import com.titanos.domain.repo.ModeRepository
import com.titanos.domain.repo.SystemRepository
import com.titanos.domain.usecase.ActivateGameModeUseCase
import com.titanos.domain.usecase.ObserveModeAndMetricsUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class TitanContainer(context: Context) {
    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://opensky-network.org/api/")
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val aircraftApi: OpenSkyApi = retrofit.create(OpenSkyApi::class.java)

    val modeRepository: ModeRepository = ModeDataStoreRepository(appContext)
    val systemRepository: SystemRepository = AndroidSystemRepository(appContext)
    val aircraftRepository: AircraftRepository = OpenSkyRepository(aircraftApi)

    val activateGameModeUseCase = ActivateGameModeUseCase(modeRepository, systemRepository)
    val observeModeAndMetricsUseCase = ObserveModeAndMetricsUseCase(modeRepository, systemRepository)
}
