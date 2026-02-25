package com.titanos.feature.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.titanos.domain.model.AppInfo
import com.titanos.domain.repo.SystemRepository

class LauncherViewModel(
    private val systemRepository: SystemRepository
) : ViewModel() {
    val apps: List<AppInfo> = systemRepository.installedApps()

    val steamPackages = setOf(
        "com.valvesoftware.steamlink",
        "com.nvidia.gamestream"
    )

    fun isSteamOrGame(app: AppInfo): Boolean = app.packageName in steamPackages || app.isGame || app.supportsProton

    companion object {
        fun factory(systemRepository: SystemRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LauncherViewModel(systemRepository) as T
                }
            }
    }
}
