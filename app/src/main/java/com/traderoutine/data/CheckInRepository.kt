package com.traderoutine.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class CheckInRepository(private val store: CheckInStore) {
    val settingsFlow: Flow<AppSettings> = store.settingsFlow
    val recordsFlow: Flow<List<DayRecord>> = store.recordsFlow

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        val current = store.settingsFlow.first()
        store.updateSettings(update(current))
    }

    suspend fun updateRecord(record: DayRecord) {
        val records = store.recordsFlow.first()
        val updated = records.filterNot { it.dateIso == record.dateIso } + record
        store.saveRecords(updated)
    }

    suspend fun clearAll() {
        store.clearAll()
    }

    fun recordForDate(date: LocalDate): Flow<DayRecord?> {
        val iso = date.toString()
        return store.recordsFlow.map { records -> records.firstOrNull { it.dateIso == iso } }
    }
}
