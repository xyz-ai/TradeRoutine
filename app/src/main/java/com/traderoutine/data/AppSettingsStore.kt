package com.traderoutine.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.traderoutine.model.AppSettings
import com.traderoutine.model.UiLanguage
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "trade_routine_settings")

class AppSettingsStore(private val context: Context) {
    private object Keys {
        val language = stringPreferencesKey("language")
        val lastCelebratedDate = stringPreferencesKey("last_celebrated_date")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences -> preferences.toAppSettings() }

    suspend fun setLanguage(language: UiLanguage) {
        context.dataStore.edit { preferences ->
            preferences[Keys.language] = language.code
        }
    }

    suspend fun setLastCelebratedDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.lastCelebratedDate] = date
        }
    }

    suspend fun clearCelebrationState() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.lastCelebratedDate)
        }
    }

    private fun Preferences.toAppSettings(): AppSettings {
        return AppSettings(
            language = UiLanguage.fromCode(this[Keys.language]),
            lastCelebratedDate = this[Keys.lastCelebratedDate]
        )
    }
}
