package com.titanos.feature.skyview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.titanos.domain.model.Aircraft
import com.titanos.domain.repo.AircraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SkyViewViewModel(
    private val aircraftRepository: AircraftRepository
) : ViewModel() {
    private val _aircraft = MutableStateFlow<List<Aircraft>>(emptyList())
    val aircraft: StateFlow<List<Aircraft>> = _aircraft.asStateFlow()

    fun startFeed(lat: Double, lon: Double) {
        viewModelScope.launch {
            aircraftRepository.aircraftFeed(lat, lon).collect { _aircraft.value = it }
        }
    }

    companion object {
        fun factory(repo: AircraftRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SkyViewViewModel(repo) as T
                }
            }
    }
}
