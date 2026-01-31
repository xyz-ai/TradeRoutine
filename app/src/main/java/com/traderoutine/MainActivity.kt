package com.traderoutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.traderoutine.data.TradeRoutineDatabase
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.ui.MainViewModel
import com.traderoutine.ui.MainViewModelFactory
import com.traderoutine.ui.TradeRoutineApp
import com.traderoutine.ui.theme.TradeRoutineTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val database = TradeRoutineDatabase.getInstance(this)
        val repository = TradeRoutineRepository(database.dayRecordDao(), database.settingsDao())
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings = viewModel.settings.value
            TradeRoutineTheme(darkTheme = settings.darkModeEnabled) {
                TradeRoutineApp(viewModel)
            }
        }
    }
}
