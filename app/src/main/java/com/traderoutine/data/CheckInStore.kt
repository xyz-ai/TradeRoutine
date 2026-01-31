package com.traderoutine.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "trade_routine")

class CheckInStore(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    private val settingsKey = stringPreferencesKey("settings")
    private val recordsKey = stringPreferencesKey("records")

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[settingsKey]?.let { json.decodeFromString(AppSettings.serializer(), it) }
                ?: AppSettings()
        }

    val recordsFlow: Flow<List<DayRecord>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[recordsKey]?.let { json.decodeFromString(ListSerializer.DayRecordList, it) }
                ?: emptyList()
        }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[settingsKey] = json.encodeToString(settings)
        }
    }

    suspend fun saveRecords(records: List<DayRecord>) {
        context.dataStore.edit { prefs ->
            prefs[recordsKey] = json.encodeToString(ListSerializer.DayRecordList, records)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

object ListSerializer {
    val DayRecordList = kotlinx.serialization.builtins.ListSerializer(DayRecord.serializer())
}
