package com.traderoutine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.traderoutine.data.DayRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun RecordsScreen(viewModel: MainViewModel, contentPadding: PaddingValues) {
    val month = YearMonth.now()
    val monthRecords by viewModel.recordsForMonth(month).collectAsState(initial = emptyList())
    val settings by viewModel.settings.collectAsState()
    val last7Start = LocalDate.now().minusDays(6)
    val last7Records by viewModel.recordsBetween(last7Start, LocalDate.now())
        .collectAsState(initial = emptyList())

    val recordMap = monthRecords.associateBy { it.date }
    val streak = calculateStreak(recordMap, LocalDate.now(), settings.includeWeekend)

    LazyColumn(
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "打卡记录",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "连续打卡 ${streak} 天",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${month.year} 年 ${month.monthValue} 月",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CalendarGrid(month = month, records = recordMap)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "最近 7 天",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    last7Records.sortedBy { it.date }.forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = record.date.toString())
                            Text(text = statusLabel(record))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(month: YearMonth, records: Map<LocalDate, DayRecord>) {
    val firstDay = month.atDay(1)
    val firstDayOfWeek = firstDay.dayOfWeek
    val totalDays = month.lengthOfMonth()
    val offset = (firstDayOfWeek.value % 7)
    val weeks = ((offset + totalDays) / 7) + if ((offset + totalDays) % 7 == 0) 0 else 1

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.entries.forEach { day ->
                Text(
                    text = day.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.CHINA),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        repeat(weeks) { weekIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayIndex ->
                    val dayNumber = weekIndex * 7 + dayIndex - offset + 1
                    val date = if (dayNumber in 1..totalDays) month.atDay(dayNumber) else null
                    val color = when {
                        date == null -> Color.Transparent
                        else -> statusColor(records[date])
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(color = color, shape = MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = date?.dayOfMonth?.toString().orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendItem(color = Color(0xFF7BC96F), label = "完成 ≥3")
            LegendItem(color = Color(0xFFF2C94C), label = "完成 1-2")
            LegendItem(color = Color(0xFFE0E0E0), label = "未打卡")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = MaterialTheme.shapes.extraSmall),
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun statusColor(record: DayRecord?): Color {
    if (record == null) return Color(0xFFE0E0E0)
    return when (record.completedModulesCount()) {
        0 -> Color(0xFFE0E0E0)
        in 1..2 -> Color(0xFFF2C94C)
        else -> Color(0xFF7BC96F)
    }
}

private fun statusLabel(record: DayRecord): String {
    return when (record.completedModulesCount()) {
        0 -> "未打卡"
        in 1..2 -> "完成 1-2"
        else -> "完成 ≥3"
    }
}

private fun calculateStreak(
    records: Map<LocalDate, DayRecord>,
    today: LocalDate,
    includeWeekend: Boolean,
): Int {
    var current = today
    var streak = 0
    while (true) {
        if (!includeWeekend && current.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
            current = current.minusDays(1)
            continue
        }
        val record = records[current]
        val completed = record?.completedModulesCount() ?: 0
        if (completed >= 3) {
            streak += 1
            current = current.minusDays(1)
        } else {
            break
        }
    }
    return streak
}
