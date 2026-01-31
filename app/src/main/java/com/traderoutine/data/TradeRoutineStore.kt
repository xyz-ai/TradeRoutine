package com.traderoutine.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "trade_routine")

class TradeRoutineStore(private val context: Context) {
    private val json = Json { encodeDefaults = true }

    val state: Flow<AppState> = context.dataStore.data.map { preferences ->
        val recordsJson = preferences[KEY_RECORDS] ?: "{}"
        val records = runCatching {
            json.decodeFromString<Map<String, DayRecord>>(recordsJson)
        }.getOrElse { emptyMap() }

        val settings = SettingsState(
            reminderEnabled = preferences[KEY_REMINDER] ?: true,
            darkMode = preferences[KEY_DARK_MODE] ?: false,
            weekendIncluded = preferences[KEY_WEEKEND] ?: true
        )

        AppState(records = records, settings = settings)
    }

    suspend fun toggleTask(date: LocalDate, taskId: TaskId) {
        context.dataStore.edit { prefs ->
            val updated = prefs.updateRecord(date) { record ->
                val mutable = record.checkedTasks.toMutableSet()
                if (mutable.contains(taskId)) {
                    mutable.remove(taskId)
                } else {
                    mutable.add(taskId)
                }
                record.copy(checkedTasks = mutable)
            }
            prefs[KEY_RECORDS] = json.encodeToString(updated)
        }
    }

    suspend fun updateWrapUp(date: LocalDate, note: String, mood: Mood?) {
        context.dataStore.edit { prefs ->
            val updated = prefs.updateRecord(date) { record ->
                record.copy(wrapUpNote = note, mood = mood)
            }
            prefs[KEY_RECORDS] = json.encodeToString(updated)
        }
    }

    suspend fun updateSettings(transform: (SettingsState) -> SettingsState) {
        context.dataStore.edit { prefs ->
            val current = SettingsState(
                reminderEnabled = prefs[KEY_REMINDER] ?: true,
                darkMode = prefs[KEY_DARK_MODE] ?: false,
                weekendIncluded = prefs[KEY_WEEKEND] ?: true
            )
            val updated = transform(current)
            prefs[KEY_REMINDER] = updated.reminderEnabled
            prefs[KEY_DARK_MODE] = updated.darkMode
            prefs[KEY_WEEKEND] = updated.weekendIncluded
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun Preferences.updateRecord(
        date: LocalDate,
        transform: (DayRecord) -> DayRecord
    ): Map<String, DayRecord> {
        val recordsJson = this[KEY_RECORDS] ?: "{}"
        val records = runCatching {
            json.decodeFromString<Map<String, DayRecord>>(recordsJson)
        }.getOrElse { emptyMap() }
        val key = date.toString()
        val current = records[key] ?: DayRecord(date = key)
        return records.toMutableMap().apply {
            this[key] = transform(current)
        }
    }

    companion object {
        private val KEY_RECORDS = stringPreferencesKey("records_json")
        private val KEY_REMINDER = booleanPreferencesKey("reminder_enabled")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_WEEKEND = booleanPreferencesKey("weekend_included")
    }
}
