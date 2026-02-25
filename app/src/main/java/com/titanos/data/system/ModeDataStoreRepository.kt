package com.titanos.data.system

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.titanos.domain.model.ModeType
import com.titanos.domain.repo.ModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.modeDataStore by preferencesDataStore(name = "titan_mode")

class ModeDataStoreRepository(
    private val context: Context
) : ModeRepository {

    private val modeKey = stringPreferencesKey("active_mode")

    override val activeMode: Flow<ModeType> = context.modeDataStore.data.map { prefs ->
        prefs[modeKey]
            ?.let { saved -> ModeType.entries.firstOrNull { it.name == saved } }
            ?: ModeType.Normal
    }

    override suspend fun setMode(mode: ModeType) {
        context.modeDataStore.edit { prefs ->
            prefs[modeKey] = mode.name
        }
    }
}
