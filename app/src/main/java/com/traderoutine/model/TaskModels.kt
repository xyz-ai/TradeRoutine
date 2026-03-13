package com.traderoutine.model

import java.time.LocalDate

data class TaskTemplateInput(
    val title: String,
    val note: String,
    val durationText: String,
    val startTime: String,
    val endTime: String,
)

data class TaskTemplate(
    val id: Long,
    val title: String,
    val note: String,
    val durationText: String,
    val startTime: String,
    val endTime: String,
    val sortOrder: Int,
)

data class DailyTaskItem(
    val date: LocalDate,
    val templateId: Long,
    val title: String,
    val note: String,
    val durationText: String,
    val startTime: String,
    val endTime: String,
    val isCompleted: Boolean,
    val orderIndex: Int,
)

data class AppSettings(
    val language: UiLanguage = UiLanguage.ENGLISH,
    val lastCelebratedDate: String? = null,
)
