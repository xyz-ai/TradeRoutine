package com.traderoutine.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTemplateDao {
    @Query("SELECT * FROM task_templates ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<TaskTemplateEntity>>

    @Query("SELECT * FROM task_templates ORDER BY sortOrder ASC, id ASC")
    suspend fun getAll(): List<TaskTemplateEntity>

    @Query("SELECT * FROM task_templates WHERE id = :templateId LIMIT 1")
    suspend fun getById(templateId: Long): TaskTemplateEntity?

    @Query("SELECT MAX(sortOrder) FROM task_templates")
    suspend fun getMaxSortOrder(): Int?

    @Insert
    suspend fun insert(template: TaskTemplateEntity): Long

    @Update
    suspend fun update(template: TaskTemplateEntity)

    @Query("DELETE FROM task_templates WHERE id = :templateId")
    suspend fun deleteById(templateId: Long)

    @Query("DELETE FROM task_templates")
    suspend fun clearAll()
}
