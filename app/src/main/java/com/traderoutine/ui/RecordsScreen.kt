package com.traderoutine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.traderoutine.data.AppState
import com.traderoutine.data.ModulesCatalog
import com.traderoutine.data.TaskId
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun RecordsScreen(appState: AppState) {
    val today = LocalDate.now()
    val yearMonth = YearMonth.now()
    val records = appState.records
    val weekendIncluded = appState.settings.weekendIncluded

    val streak = calculateStreak(records, today, weekendIncluded)
    val last7 = (0..6).map { today.minusDays(it.toLong()) }.reversed()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "打卡记录", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "连续打卡天数：$streak", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        MonthCalendar(yearMonth = yearMonth, records = records)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "最近 7 天", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        last7.forEach { date ->
            val status = statusFor(date, records)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = date.toString())
                Text(text = status.label, color = status.color)
            }
        }
    }
}

@Composable
private fun MonthCalendar(yearMonth: YearMonth, records: Map<String, com.traderoutine.data.DayRecord>) {
    val firstDay = yearMonth.atDay(1)
    val lengthOfMonth = yearMonth.lengthOfMonth()
    val firstWeekdayIndex = (firstDay.dayOfWeek.value % 7)
    val days = (1..lengthOfMonth).map { yearMonth.atDay(it) }

    Text(
        text = "${yearMonth.year}年${yearMonth.monthValue}月",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("日", "一", "二", "三", "四", "五", "六").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp))

    var index = 0
    val totalCells = ((firstWeekdayIndex + lengthOfMonth + 6) / 7) * 7
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { column ->
                    val dayNumber = index - firstWeekdayIndex + 1
                    val date = days.getOrNull(dayNumber - 1)
                    val status = if (date != null) statusFor(date, records) else null
                    CalendarCell(
                        day = if (date != null) dayNumber.toString() else "",
                        color = status?.color ?: Color.Transparent,
                        modifier = Modifier.weight(1f)
                    )
                    index += 1
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(day: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day)
        Spacer(modifier = Modifier.height(4.dp))
        Spacer(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(50))
        )
    }
}

private data class StatusBadge(val label: String, val color: Color)

private fun statusFor(date: LocalDate, records: Map<String, com.traderoutine.data.DayRecord>): StatusBadge {
    val record = records[date.toString()]
    val completed = countModules(record?.checkedTasks.orEmpty())
    return when {
        completed >= 3 -> StatusBadge("完成", Color(0xFF4CAF50))
        completed in 1..2 -> StatusBadge("部分", Color(0xFFFFC107))
        else -> StatusBadge("未打卡", Color(0xFFBDBDBD))
    }
}

private fun countModules(checked: Set<TaskId>): Int {
    return ModulesCatalog.modules.count { module ->
        module.tasks.any { checked.contains(it.id) }
    }
}

private fun calculateStreak(
    records: Map<String, com.traderoutine.data.DayRecord>,
    today: LocalDate,
    weekendIncluded: Boolean
): Int {
    var count = 0
    var cursor = today
    while (true) {
        if (!weekendIncluded && (cursor.dayOfWeek == DayOfWeek.SATURDAY || cursor.dayOfWeek == DayOfWeek.SUNDAY)) {
            cursor = cursor.minusDays(1)
            continue
        }
        val record = records[cursor.toString()]
        val completed = countModules(record?.checkedTasks.orEmpty())
        if (completed >= 3) {
            count += 1
            cursor = cursor.minusDays(1)
        } else {
            break
        }
    }
    return count
}
