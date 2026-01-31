package com.traderoutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.traderoutine.ui.TradeRoutineApp
import com.traderoutine.ui.rememberRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TradeRoutineRoot()
        }
    }
}

@Composable
private fun TradeRoutineRoot() {
    val repository = rememberRepository(LocalContext.current)
    TradeRoutineApp(repository)
}
