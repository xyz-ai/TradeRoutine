package com.traderoutine

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.traderoutine.core.AppContainer
import com.traderoutine.core.TradeRoutineViewModelFactory
import com.traderoutine.ui.navigation.Routes
import com.traderoutine.ui.screens.calendar.CalendarScreen
import com.traderoutine.ui.screens.calendar.CalendarViewModel
import com.traderoutine.ui.screens.calendar.DayDetailScreen
import com.traderoutine.ui.screens.calendar.DayDetailViewModel
import com.traderoutine.ui.screens.settings.AboutScreen
import com.traderoutine.ui.screens.settings.DisclaimerScreen
import com.traderoutine.ui.screens.settings.SettingsScreen
import com.traderoutine.ui.screens.settings.SettingsViewModel
import com.traderoutine.ui.screens.tasks.TaskEditorScreen
import com.traderoutine.ui.screens.tasks.TaskEditorViewModel
import com.traderoutine.ui.screens.tasks.TasksScreen
import com.traderoutine.ui.screens.tasks.TasksViewModel
import com.traderoutine.ui.strings.LocalAppStrings
import com.traderoutine.ui.strings.stringsFor
import com.traderoutine.ui.theme.TradeRoutineTheme
import kotlinx.coroutines.launch

@Composable
fun TradeRoutineApp() {
    val context = LocalContext.current
    val container = remember(context) { AppContainer(context.applicationContext) }
    val factory = remember(container) { TradeRoutineViewModelFactory(container) }
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val strings = remember(settingsState.language) { stringsFor(settingsState.language) }

    TradeRoutineTheme {
        CompositionLocalProvider(LocalAppStrings provides strings) {
            AppLifecycleSync(container = container)
            TradeRoutineNavHost(factory = factory)
        }
    }
}

@Composable
private fun AppLifecycleSync(container: AppContainer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    DisposableEffect(lifecycleOwner, container) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    container.repository.refreshCurrentDate()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(container) {
        container.repository.refreshCurrentDate()
    }
}

@Composable
private fun TradeRoutineNavHost(factory: TradeRoutineViewModelFactory) {
    val strings = LocalAppStrings.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val topLevelRoutes = setOf(Routes.TASKS, Routes.CALENDAR, Routes.SETTINGS)
    val showBottomBar = currentDestination?.route in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Routes.TASKS, strings.tasksTab, Icons.Outlined.CheckCircle),
                        Triple(Routes.CALENDAR, strings.calendarTab, Icons.Outlined.CalendarMonth),
                        Triple(Routes.SETTINGS, strings.settingsTab, Icons.Outlined.Settings)
                    )
                    items.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            label = { Text(text = label) }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.union(WindowInsets.navigationBars)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TASKS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.TASKS) {
                val viewModel: TasksViewModel = viewModel(factory = factory)
                TasksScreen(
                    viewModel = viewModel,
                    onAddTask = { navController.navigate(Routes.taskEditor()) },
                    onEditTask = { templateId -> navController.navigate(Routes.taskEditor(templateId)) }
                )
            }
            composable(
                route = "${Routes.TASK_EDITOR}?${Routes.TEMPLATE_ID}={${Routes.TEMPLATE_ID}}",
                arguments = listOf(
                    navArgument(Routes.TEMPLATE_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                val viewModel: TaskEditorViewModel = viewModel(factory = factory)
                TaskEditorScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CALENDAR) {
                val viewModel: CalendarViewModel = viewModel(factory = factory)
                CalendarScreen(
                    viewModel = viewModel,
                    onOpenDay = { date -> navController.navigate(Routes.dayDetail(date.toString())) }
                )
            }
            composable(
                route = "${Routes.DAY_DETAIL}/{${Routes.DATE}}",
                arguments = listOf(navArgument(Routes.DATE) { type = NavType.StringType })
            ) {
                val viewModel: DayDetailViewModel = viewModel(factory = factory)
                DayDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                val viewModel: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(
                    viewModel = viewModel,
                    onOpenAbout = { navController.navigate(Routes.ABOUT) },
                    onOpenDisclaimer = { navController.navigate(Routes.DISCLAIMER) }
                )
            }
            composable(Routes.ABOUT) {
                AboutScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Routes.DISCLAIMER) {
                DisclaimerScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
