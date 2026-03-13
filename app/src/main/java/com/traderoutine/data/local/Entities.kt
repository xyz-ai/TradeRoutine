package com.traderoutine.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "task_templates")
data class TaskTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val note: String,
    val durationText: String,
    val startTime: String,
    val endTime: String,
    val sortOrder: Int,
)

@Entity(
    tableName = "daily_task_records",
    primaryKeys = ["date", "templateId"],
    indices = [Index(value = ["date"])]
)
data class DailyTaskRecordEntity(
    val date: String,
    val templateId: Long,
    val titleSnapshot: String,
    val noteSnapshot: String,
    val durationTextSnapshot: String,
    val startTimeSnapshot: String,
    val endTimeSnapshot: String,
    val isCompleted: Boolean,
    val orderIndex: Int,
)
