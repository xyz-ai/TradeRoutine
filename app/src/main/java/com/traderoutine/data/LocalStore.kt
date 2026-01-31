package com.traderoutine.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

@Entity(tableName = "day_records")
data class DayRecordEntity(
    @PrimaryKey val date: String,
    val moduleItemsEncoded: String,
    val mood: String?,
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val reminderEnabled: Boolean,
    val darkModeEnabled: Boolean,
    val includeWeekend: Boolean,
)

class Converters {
    @TypeConverter
    fun fromMood(value: String?): MoodStatus? = value?.let { MoodStatus.valueOf(it) }

    @TypeConverter
    fun moodToString(value: MoodStatus?): String? = value?.name
}

@Dao
interface DayRecordDao {
    @Query("SELECT * FROM day_records WHERE date = :date")
    fun recordByDate(date: String): Flow<DayRecordEntity?>

    @Query("SELECT * FROM day_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun recordsBetween(startDate: String, endDate: String): Flow<List<DayRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: DayRecordEntity)

    @Query("DELETE FROM day_records")
    suspend fun clearAll()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    fun settings(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: SettingsEntity)

    @Query("DELETE FROM settings")
    suspend fun clear()
}

@Database(
    entities = [DayRecordEntity::class, SettingsEntity::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class TradeRoutineDatabase : RoomDatabase() {
    abstract fun dayRecordDao(): DayRecordDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var instance: TradeRoutineDatabase? = null

        fun getInstance(context: Context): TradeRoutineDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TradeRoutineDatabase::class.java,
                    "trade_routine.db",
                ).build().also { instance = it }
            }
    }
}

class TradeRoutineRepository(
    private val dayRecordDao: DayRecordDao,
    private val settingsDao: SettingsDao,
) {
    fun recordForDate(date: LocalDate): Flow<DayRecord> =
        dayRecordDao.recordByDate(date.format(dateFormatter)).map { entity ->
            entity?.let { decodeRecord(it) } ?: DayRecord(
                date = date,
                moduleItemStates = ModuleCatalog.defaultModuleStates(),
                moodStatus = null,
            )
        }

    fun recordsForMonth(month: YearMonth): Flow<List<DayRecord>> {
        val start = month.atDay(1).format(dateFormatter)
        val end = month.atEndOfMonth().format(dateFormatter)
        return dayRecordDao.recordsBetween(start, end).map { entities ->
            entities.map { decodeRecord(it) }
        }
    }

    fun recordsBetween(start: LocalDate, end: LocalDate): Flow<List<DayRecord>> {
        return dayRecordDao.recordsBetween(
            start.format(dateFormatter),
            end.format(dateFormatter),
        ).map { entities -> entities.map { decodeRecord(it) } }
    }

    suspend fun updateRecord(record: DayRecord) {
        dayRecordDao.upsert(encodeRecord(record))
    }

    fun settings(): Flow<SettingsEntity> = settingsDao.settings().map { entity ->
        entity ?: SettingsEntity(
            reminderEnabled = false,
            darkModeEnabled = false,
            includeWeekend = true,
        )
    }

    suspend fun updateSettings(settings: SettingsEntity) {
        settingsDao.upsert(settings)
    }

    suspend fun clearAll() {
        dayRecordDao.clearAll()
        settingsDao.clear()
    }

    private fun encodeRecord(record: DayRecord): DayRecordEntity {
        val encodedModules = record.moduleItemStates.joinToString("|") { moduleItems ->
            moduleItems.joinToString("") { if (it) "1" else "0" }
        }
        return DayRecordEntity(
            date = record.date.format(dateFormatter),
            moduleItemsEncoded = encodedModules,
            mood = record.moodStatus?.name,
        )
    }

    private fun decodeRecord(entity: DayRecordEntity): DayRecord {
        val moduleStates = parseModuleStates(entity.moduleItemsEncoded)
        return DayRecord(
            date = LocalDate.parse(entity.date, dateFormatter),
            moduleItemStates = moduleStates,
            moodStatus = entity.mood?.let { MoodStatus.valueOf(it) },
        )
    }

    private fun parseModuleStates(encoded: String): List<List<Boolean>> {
        val moduleDefinitions = ModuleCatalog.modules
        if (encoded.isBlank()) {
            return ModuleCatalog.defaultModuleStates()
        }
        val moduleStrings = encoded.split("|")
        return moduleDefinitions.mapIndexed { index, module ->
            val raw = moduleStrings.getOrNull(index).orEmpty()
            module.items.mapIndexed { itemIndex, _ -> raw.getOrNull(itemIndex) == '1' }
        }
    }
}
