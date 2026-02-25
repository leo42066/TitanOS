package com.titanos.feature.gamemode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.titanos.domain.usecase.ActivateGameModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameModeViewModel(
    private val activateGameModeUseCase: ActivateGameModeUseCase
) : ViewModel() {

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun setGameMode(enabled: Boolean) {
        _enabled.value = enabled
        viewModelScope.launch {
            activateGameModeUseCase(enabled)
        }
    }

    companion object {
        fun factory(useCase: ActivateGameModeUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameModeViewModel(useCase) as T
                }
            }
    }
}
