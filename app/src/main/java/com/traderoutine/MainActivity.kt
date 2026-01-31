package com.traderoutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.traderoutine.data.TradeRoutineStore
import com.traderoutine.ui.AppRoot
import com.traderoutine.ui.TradeRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val store = TradeRoutineStore(LocalContext.current)
            val appState by store.state.collectAsState()

            TradeRoutineTheme(darkTheme = appState.settings.darkMode) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppRoot(store = store, appState = appState)
                }
            }
        }
    }
}
