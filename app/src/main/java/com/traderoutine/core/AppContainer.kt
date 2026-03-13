package com.traderoutine.core

import android.content.Context
import com.traderoutine.data.AppSettingsStore
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.data.local.AppDatabase

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val settingsStore = AppSettingsStore(context)

    val repository = TradeRoutineRepository(
        database = database,
        settingsStore = settingsStore
    )
}
