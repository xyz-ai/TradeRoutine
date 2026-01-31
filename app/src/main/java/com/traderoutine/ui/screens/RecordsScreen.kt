package com.traderoutine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.traderoutine.data.DayRecord
import com.traderoutine.ui.AppViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun RecordsScreen(modifier: Modifier = Modifier, viewModel: AppViewModel) {
    val records by viewModel.records.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val today = LocalDate.now()
    val streak = calculateStreak(records, settings.weekendIncluded)
    val last7Days = (0..6).map { today.minusDays(it.toLong()) }.reversed()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("打卡记录", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("连续打卡", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$streak 天", style = MaterialTheme.typography.headlineSmall)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("最近 7 天", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    last7Days.forEach { date ->
                        val count = recordCount(records, date)
                        val color = statusColor(count)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(date.dayOfWeek.name.take(3), style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Spacer(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("本月概览", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                CalendarView(records = records)
            }
        }
    }
}

@Composable
private fun CalendarView(records: List<DayRecord>) {
    val today = LocalDate.now()
    val yearMonth = YearMonth.from(today)
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startOffset = (firstDay.dayOfWeek.value % 7)
    val totalCells = ((startOffset + daysInMonth) / 7 + 1) * 7
    val formatter = DateTimeFormatter.ofPattern("d")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val dayLabels = listOf("一", "二", "三", "四", "五", "六", "日")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dayLabels.forEach { label ->
                Text(label, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        var day = 1
        for (row in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < startOffset || day > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f).height(32.dp))
                    } else {
                        val date = yearMonth.atDay(day)
                        val count = recordCount(records, date)
                        val color = statusColor(count)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(formatter.format(date))
                            Spacer(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color, CircleShape)
                            )
                        }
                        day += 1
                    }
                }
            }
        }
    }
}

private fun recordCount(records: List<DayRecord>, date: LocalDate): Int {
    return records.firstOrNull { it.dateIso == date.toString() }?.completedModules?.size ?: 0
}

private fun statusColor(count: Int): Color {
    return when {
        count >= 3 -> Color(0xFF22C55E)
        count in 1..2 -> Color(0xFFEAB308)
        else -> Color(0xFFD1D5DB)
    }
}

private fun calculateStreak(records: List<DayRecord>, weekendIncluded: Boolean): Int {
    var streak = 0
    var date = LocalDate.now()

    while (true) {
        if (!weekendIncluded && (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY)) {
            date = date.minusDays(1)
            continue
        }
        val count = recordCount(records, date)
        if (count >= 3) {
            streak += 1
            date = date.minusDays(1)
        } else {
            break
        }
    }
    return streak
}
