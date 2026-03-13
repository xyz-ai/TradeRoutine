package com.traderoutine.ui.strings

import androidx.compose.runtime.staticCompositionLocalOf
import com.traderoutine.model.DailyStatus
import com.traderoutine.model.UiLanguage
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class AppStrings(
    val appName: String,
    val tasksTab: String,
    val calendarTab: String,
    val settingsTab: String,
    val todaysRoutine: String,
    val calmSummaryPrefix: String,
    val addTask: String,
    val editTask: String,
    val deleteTask: String,
    val deleteTaskConfirmTitle: String,
    val deleteTaskConfirmBody: String,
    val cancel: String,
    val confirm: String,
    val save: String,
    val taskTitle: String,
    val taskNote: String,
    val durationText: String,
    val startTime: String,
    val endTime: String,
    val titleRequired: String,
    val noTasksTitle: String,
    val noTasksBody: String,
    val monthTitleFormatter: (YearMonth) -> String,
    val weekdayLabels: List<String>,
    val dayDetailTitle: String,
    val historyEmpty: String,
    val language: String,
    val languageEnglish: String,
    val languageChinese: String,
    val clearLocalData: String,
    val clearLocalDataBody: String,
    val aboutApp: String,
    val disclaimer: String,
    val aboutDescription: String,
    val disclaimerBody: String,
    val statusNotStarted: String,
    val statusInProgress: String,
    val statusCompleted: String,
    val completedDialogTitle: String,
    val finishedLabel: String,
    val unfinishedLabel: String,
    val versionLabel: String,
    val todayLabel: String,
    val readOnlyHistoryLabel: String,
    val noHistoryRecord: String,
    val taskCountFormat: (Int) -> String,
    val completedCountFormat: (Int, Int) -> String,
    val dateFormatter: (LocalDate) -> String,
)

val LocalAppStrings = staticCompositionLocalOf { englishStrings() }

fun stringsFor(language: UiLanguage): AppStrings {
    return when (language) {
        UiLanguage.ENGLISH -> englishStrings()
        UiLanguage.CHINESE -> chineseStrings()
    }
}

fun AppStrings.statusText(status: DailyStatus): String {
    return when (status) {
        DailyStatus.NOT_STARTED -> statusNotStarted
        DailyStatus.IN_PROGRESS -> statusInProgress
        DailyStatus.COMPLETED -> statusCompleted
    }
}

private fun englishStrings(): AppStrings {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    return AppStrings(
        appName = "TradeRoutine",
        tasksTab = "Tasks",
        calendarTab = "Calendar",
        settingsTab = "Settings",
        todaysRoutine = "Today's routine",
        calmSummaryPrefix = "A quiet list for today",
        addTask = "Add task",
        editTask = "Edit",
        deleteTask = "Delete",
        deleteTaskConfirmTitle = "Delete this task template?",
        deleteTaskConfirmBody = "This removes it from today's list immediately. Past records stay read-only.",
        cancel = "Cancel",
        confirm = "Confirm",
        save = "Save",
        taskTitle = "Title",
        taskNote = "Note",
        durationText = "Duration text",
        startTime = "Start time",
        endTime = "End time",
        titleRequired = "Title is required.",
        noTasksTitle = "No routines yet",
        noTasksBody = "Create your first daily task template to begin today's check-in.",
        monthTitleFormatter = { month -> month.format(monthFormatter) },
        weekdayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        dayDetailTitle = "Day detail",
        historyEmpty = "No record for this date.",
        language = "Language",
        languageEnglish = "English",
        languageChinese = "Chinese",
        clearLocalData = "Clear local data",
        clearLocalDataBody = "This removes all task templates and history stored on this device.",
        aboutApp = "About",
        disclaimer = "Disclaimer",
        aboutDescription = "A minimal daily routine check-in app for keeping the day steady and clear.",
        disclaimerBody = "All data stays on this device only.\n\nNo personal data is collected or transmitted.\n\nClearing app data or uninstalling may remove your records.",
        statusNotStarted = "Not started",
        statusInProgress = "In progress",
        statusCompleted = "Completed",
        completedDialogTitle = "Today is complete",
        finishedLabel = "Finished",
        unfinishedLabel = "To finish",
        versionLabel = "Version",
        todayLabel = "Today",
        readOnlyHistoryLabel = "History is read-only.",
        noHistoryRecord = "There was no saved task record for this date.",
        taskCountFormat = { count -> "$count tasks" },
        completedCountFormat = { completed, total -> "$completed of $total finished" },
        dateFormatter = { date -> date.format(dateFormatter) }
    )
}

private fun chineseStrings(): AppStrings {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy年M月")
    return AppStrings(
        appName = "TradeRoutine",
        tasksTab = "任务",
        calendarTab = "日历",
        settingsTab = "设置",
        todaysRoutine = "今日例行",
        calmSummaryPrefix = "今天只保留一份安静的清单",
        addTask = "添加任务",
        editTask = "编辑",
        deleteTask = "删除",
        deleteTaskConfirmTitle = "删除这个任务模板？",
        deleteTaskConfirmBody = "删除后会立刻从今天的列表中移除，历史记录仍保留为只读。",
        cancel = "取消",
        confirm = "确认",
        save = "保存",
        taskTitle = "标题",
        taskNote = "备注",
        durationText = "时长说明",
        startTime = "开始时间",
        endTime = "结束时间",
        titleRequired = "标题不能为空。",
        noTasksTitle = "还没有例行任务",
        noTasksBody = "先创建第一个每日任务模板，再开始今天的打卡。",
        monthTitleFormatter = { month -> month.format(monthFormatter) },
        weekdayLabels = listOf("一", "二", "三", "四", "五", "六", "日"),
        dayDetailTitle = "日期详情",
        historyEmpty = "这一天没有记录。",
        language = "语言",
        languageEnglish = "英文",
        languageChinese = "中文",
        clearLocalData = "清空本地数据",
        clearLocalDataBody = "这会删除当前设备上的所有任务模板和历史记录。",
        aboutApp = "关于应用",
        disclaimer = "免责声明",
        aboutDescription = "一个简洁的每日例行打卡应用，用来安静地完成今天。",
        disclaimerBody = "所有数据仅保存在当前设备本地。\n\n不会收集或传输任何个人数据。\n\n清除应用数据或卸载应用，可能会导致记录丢失。",
        statusNotStarted = "未开始",
        statusInProgress = "进行中",
        statusCompleted = "已完成",
        completedDialogTitle = "今天已经完成",
        finishedLabel = "已完成",
        unfinishedLabel = "待完成",
        versionLabel = "版本",
        todayLabel = "今天",
        readOnlyHistoryLabel = "历史记录仅供查看，不能修改。",
        noHistoryRecord = "这一天没有保存的任务记录。",
        taskCountFormat = { count -> "共 $count 项任务" },
        completedCountFormat = { completed, total -> "已完成 $completed / $total 项" },
        dateFormatter = { date -> date.format(dateFormatter) }
    )
}
