package com.titanos.feature.modes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.titanos.domain.model.ModeType
import com.titanos.domain.repo.ModeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModeViewModel(
    private val modeRepository: ModeRepository
) : ViewModel() {

    val activeMode: StateFlow<ModeType> = modeRepository.activeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModeType.Normal
    )

    fun setMode(modeType: ModeType) {
        viewModelScope.launch {
            modeRepository.setMode(modeType)
        }
    }

    companion object {
        fun factory(modeRepository: ModeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ModeViewModel(modeRepository) as T
                }
            }
    }
}
