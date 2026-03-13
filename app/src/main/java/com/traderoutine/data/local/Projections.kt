package com.traderoutine.data.local

data class DailyStatusSummaryEntity(
    val date: String,
    val totalCount: Int,
    val completedCount: Int,
)
