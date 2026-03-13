package com.traderoutine.model

enum class DailyStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED;

    companion object {
        fun fromCounts(totalCount: Int, completedCount: Int): DailyStatus {
            return when {
                totalCount <= 0 -> NOT_STARTED
                completedCount <= 0 -> NOT_STARTED
                completedCount >= totalCount -> COMPLETED
                else -> IN_PROGRESS
            }
        }
    }
}
