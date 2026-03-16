package com.traderoutine.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TaskTemplateDao _taskTemplateDao;

  private volatile DailyTaskRecordDao _dailyTaskRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `task_templates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `note` TEXT NOT NULL, `durationText` TEXT NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_task_records` (`date` TEXT NOT NULL, `templateId` INTEGER NOT NULL, `titleSnapshot` TEXT NOT NULL, `noteSnapshot` TEXT NOT NULL, `durationTextSnapshot` TEXT NOT NULL, `startTimeSnapshot` TEXT NOT NULL, `endTimeSnapshot` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `orderIndex` INTEGER NOT NULL, PRIMARY KEY(`date`, `templateId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_task_records_date` ON `daily_task_records` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '64e19947220b79676776c0292b398707')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `task_templates`");
        db.execSQL("DROP TABLE IF EXISTS `daily_task_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTaskTemplates = new HashMap<String, TableInfo.Column>(7);
        _columnsTaskTemplates.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("durationText", new TableInfo.Column("durationText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("startTime", new TableInfo.Column("startTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("endTime", new TableInfo.Column("endTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskTemplates.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTaskTemplates = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTaskTemplates = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTaskTemplates = new TableInfo("task_templates", _columnsTaskTemplates, _foreignKeysTaskTemplates, _indicesTaskTemplates);
        final TableInfo _existingTaskTemplates = TableInfo.read(db, "task_templates");
        if (!_infoTaskTemplates.equals(_existingTaskTemplates)) {
          return new RoomOpenHelper.ValidationResult(false, "task_templates(com.traderoutine.data.local.TaskTemplateEntity).\n"
                  + " Expected:\n" + _infoTaskTemplates + "\n"
                  + " Found:\n" + _existingTaskTemplates);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyTaskRecords = new HashMap<String, TableInfo.Column>(9);
        _columnsDailyTaskRecords.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("templateId", new TableInfo.Column("templateId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("titleSnapshot", new TableInfo.Column("titleSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("noteSnapshot", new TableInfo.Column("noteSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("durationTextSnapshot", new TableInfo.Column("durationTextSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("startTimeSnapshot", new TableInfo.Column("startTimeSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("endTimeSnapshot", new TableInfo.Column("endTimeSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTaskRecords.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyTaskRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyTaskRecords = new HashSet<TableInfo.Index>(1);
        _indicesDailyTaskRecords.add(new TableInfo.Index("index_daily_task_records_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoDailyTaskRecords = new TableInfo("daily_task_records", _columnsDailyTaskRecords, _foreignKeysDailyTaskRecords, _indicesDailyTaskRecords);
        final TableInfo _existingDailyTaskRecords = TableInfo.read(db, "daily_task_records");
        if (!_infoDailyTaskRecords.equals(_existingDailyTaskRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_task_records(com.traderoutine.data.local.DailyTaskRecordEntity).\n"
                  + " Expected:\n" + _infoDailyTaskRecords + "\n"
                  + " Found:\n" + _existingDailyTaskRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "64e19947220b79676776c0292b398707", "f47176f9b5d622ba7255fc2e909a4527");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "task_templates","daily_task_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `task_templates`");
      _db.execSQL("DELETE FROM `daily_task_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TaskTemplateDao.class, TaskTemplateDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyTaskRecordDao.class, DailyTaskRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TaskTemplateDao taskTemplateDao() {
    if (_taskTemplateDao != null) {
      return _taskTemplateDao;
    } else {
      synchronized(this) {
        if(_taskTemplateDao == null) {
          _taskTemplateDao = new TaskTemplateDao_Impl(this);
        }
        return _taskTemplateDao;
      }
    }
  }

  @Override
  public DailyTaskRecordDao dailyTaskRecordDao() {
    if (_dailyTaskRecordDao != null) {
      return _dailyTaskRecordDao;
    } else {
      synchronized(this) {
        if(_dailyTaskRecordDao == null) {
          _dailyTaskRecordDao = new DailyTaskRecordDao_Impl(this);
        }
        return _dailyTaskRecordDao;
      }
    }
  }
}
