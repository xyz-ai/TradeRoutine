package com.traderoutine.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traderoutine.model.DailyStatus
import com.traderoutine.ui.strings.LocalAppStrings
import com.traderoutine.ui.strings.statusText
import com.traderoutine.ui.theme.CalmAccentContainer
import com.traderoutine.ui.theme.CalmNotStarted
import com.traderoutine.ui.theme.CalmSuccessContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onOpenDay: (LocalDate) -> Unit,
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val monthDays = rememberMonthGrid(uiState.month)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.calendarTab) },
                actions = {
                    TextButton(onClick = viewModel::jumpToCurrentMonth) {
                        Text(text = strings.todayLabel)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                MonthHeader(
                    title = strings.monthTitleFormatter(uiState.month),
                    onPrevious = viewModel::showPreviousMonth,
                    onNext = viewModel::showNextMonth
                )
            }
            item {
                Legend()
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        strings.weekdayLabels.forEach { label ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(408.dp),
                        userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gridItems(monthDays) { day ->
                            CalendarDayCell(
                                date = day,
                                today = uiState.today,
                                status = day?.let(uiState.statuses::get) ?: DailyStatus.NOT_STARTED,
                                onClick = { selectedDate ->
                                    if (selectedDate != null) {
                                        onOpenDay(selectedDate)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onNavigateBack: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.dayDetailTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = strings.cancel
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = statusColor(uiState.status),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = strings.dateFormatter(uiState.date),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = strings.statusText(uiState.status),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = strings.readOnlyHistoryLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (uiState.isEmpty) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = strings.historyEmpty,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = strings.noHistoryRecord,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.tasks, key = { "${it.date}-${it.templateId}" }) { task ->
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (task.note.isNotBlank()) {
                                Text(
                                    text = task.note,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                            val detailLine = listOf(
                                task.durationText.takeIf { it.isNotBlank() },
                                listOf(task.startTime, task.endTime)
                                    .filter { it.isNotBlank() }
                                    .takeIf { it.isNotEmpty() }
                                    ?.joinToString(" - ")
                            ).filterNotNull().joinToString("  |  ")
                            if (detailLine.isNotBlank()) {
                                Text(
                                    text = detailLine,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Text(
                                text = if (task.isCompleted) strings.statusCompleted else strings.statusNotStarted,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    title: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun Legend() {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LegendRow(label = strings.statusNotStarted, color = CalmNotStarted)
        LegendRow(label = strings.statusInProgress, color = CalmAccentContainer)
        LegendRow(label = strings.statusCompleted, color = CalmSuccessContainer)
    }
}

@Composable
private fun LegendRow(label: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color)
                .height(16.dp)
                .fillMaxWidth(0.06f)
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    today: LocalDate,
    status: DailyStatus,
    onClick: (LocalDate?) -> Unit,
) {
    val colors = statusColor(status)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = date != null) { onClick(date) },
        color = if (date == null) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) else colors,
        shape = MaterialTheme.shapes.medium,
        border = if (date == today) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        if (date != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun rememberMonthGrid(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1)
    val leadingSlots = ((firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value) + 7) % 7
    val dates = buildList {
        repeat(leadingSlots) { add(null) }
        for (day in 1..month.lengthOfMonth()) {
            add(month.atDay(day))
        }
    }
    val trailingSlots = (7 - dates.size % 7) % 7
    return buildList {
        addAll(dates)
        repeat(trailingSlots) { add(null) }
    }
}

private fun statusColor(status: DailyStatus) = when (status) {
    DailyStatus.NOT_STARTED -> CalmNotStarted
    DailyStatus.IN_PROGRESS -> CalmAccentContainer
    DailyStatus.COMPLETED -> CalmSuccessContainer
}
