package com.traderoutine.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traderoutine.R
import com.traderoutine.data.CheckInRepository
import com.traderoutine.data.CheckInStore
import com.traderoutine.ui.screens.HomeScreen
import com.traderoutine.ui.screens.RecordsScreen
import com.traderoutine.ui.screens.SettingsScreen
import com.traderoutine.ui.theme.TradeRoutineTheme

private enum class AppTab(val label: String, val icon: Int) {
    HOME("今日", R.drawable.ic_today),
    RECORDS("记录", R.drawable.ic_calendar),
    SETTINGS("设置", R.drawable.ic_settings)
}

@Composable
fun TradeRoutineApp(repository: CheckInRepository) {
    val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository))
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    val settings by viewModel.settings.collectAsState()

    TradeRoutineTheme(darkTheme = settings.darkModeEnabled) {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    AppTab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(tab.icon),
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { padding ->
            when (currentTab) {
                AppTab.HOME -> HomeScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel
                )
                AppTab.RECORDS -> RecordsScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel
                )
                AppTab.SETTINGS -> SettingsScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun rememberRepository(context: android.content.Context): CheckInRepository {
    return remember(context) {
        CheckInRepository(CheckInStore(context))
    }
}
