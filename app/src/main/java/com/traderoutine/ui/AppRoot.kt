package com.traderoutine.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.traderoutine.R
import com.traderoutine.data.AppState
import com.traderoutine.data.TradeRoutineStore

@Composable
fun AppRoot(store: TradeRoutineStore, appState: AppState) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("home", "今日"),
        BottomNavItem("records", "记录"),
        BottomNavItem("settings", "设置")
    )

    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(store = store, appState = appState)
            }
            composable("records") {
                RecordsScreen(appState = appState)
            }
            composable("settings") {
                SettingsScreen(store = store, settings = appState.settings)
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val iconRes: Int
) {
    constructor(route: String, label: String) : this(route, label, iconRes = iconFor(route))

    companion object {
        private fun iconFor(route: String): Int {
            return when (route) {
                "home" -> R.drawable.ic_check
                "records" -> R.drawable.ic_calendar
                else -> R.drawable.ic_settings
            }
        }
    }
}
