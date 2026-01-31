package com.traderoutine.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.traderoutine.R

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val iconRes: Int,
)

@Composable
fun TradeRoutineApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val destinations = listOf(
        TopLevelDestination("home", "今日", R.drawable.ic_today),
        TopLevelDestination("records", "记录", R.drawable.ic_calendar),
        TopLevelDestination("settings", "设置", R.drawable.ic_settings),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(destination.iconRes),
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            TradeRoutineNavHost(navController, viewModel, contentPadding = PaddingValues(16.dp))
        }
    }
}

@Composable
private fun TradeRoutineNavHost(
    navController: androidx.navigation.NavHostController,
    viewModel: MainViewModel,
    contentPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(viewModel = viewModel, contentPadding = contentPadding)
        }
        composable("records") {
            RecordsScreen(viewModel = viewModel, contentPadding = contentPadding)
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, contentPadding = contentPadding)
        }
    }
}
