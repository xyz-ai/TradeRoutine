package com.traderoutine.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTaskRecordDao {
    @Query(
        """
        SELECT * FROM daily_task_records
        WHERE date = :date
        ORDER BY isCompleted ASC, orderIndex ASC, templateId ASC
        """
    )
    fun observeByDate(date: String): Flow<List<DailyTaskRecordEntity>>

    @Query(
        """
        SELECT * FROM daily_task_records
        WHERE date = :date
        ORDER BY orderIndex ASC, templateId ASC
        """
    )
    suspend fun getByDate(date: String): List<DailyTaskRecordEntity>

    @Query(
        """
        SELECT * FROM daily_task_records
        WHERE date = :date AND templateId = :templateId
        LIMIT 1
        """
    )
    suspend fun getRecord(date: String, templateId: Long): DailyTaskRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: DailyTaskRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(records: List<DailyTaskRecordEntity>)

    @Query(
        """
        UPDATE daily_task_records
        SET isCompleted = :isCompleted
        WHERE date = :date AND templateId = :templateId
        """
    )
    suspend fun updateCompletion(date: String, templateId: Long, isCompleted: Boolean)

    @Query("DELETE FROM daily_task_records WHERE date = :date AND templateId = :templateId")
    suspend fun deleteByDateAndTemplateId(date: String, templateId: Long)

    @Query("DELETE FROM daily_task_records WHERE date = :date AND templateId IN (:templateIds)")
    suspend fun deleteByDateAndTemplateIds(date: String, templateIds: List<Long>)

    @Query(
        """
        SELECT date, COUNT(*) AS totalCount, SUM(CASE WHEN isCompleted THEN 1 ELSE 0 END) AS completedCount
        FROM daily_task_records
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        """
    )
    fun observeSummariesBetween(startDate: String, endDate: String): Flow<List<DailyStatusSummaryEntity>>

    @Query("DELETE FROM daily_task_records")
    suspend fun clearAll()
}
