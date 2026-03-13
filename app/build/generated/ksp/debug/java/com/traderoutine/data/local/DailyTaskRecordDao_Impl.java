package com.traderoutine.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyTaskRecordDao_Impl implements DailyTaskRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyTaskRecordEntity> __insertionAdapterOfDailyTaskRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCompletion;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByDateAndTemplateId;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public DailyTaskRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyTaskRecordEntity = new EntityInsertionAdapter<DailyTaskRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_task_records` (`date`,`templateId`,`titleSnapshot`,`noteSnapshot`,`durationTextSnapshot`,`startTimeSnapshot`,`endTimeSnapshot`,`isCompleted`,`orderIndex`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyTaskRecordEntity entity) {
        statement.bindString(1, entity.getDate());
        statement.bindLong(2, entity.getTemplateId());
        statement.bindString(3, entity.getTitleSnapshot());
        statement.bindString(4, entity.getNoteSnapshot());
        statement.bindString(5, entity.getDurationTextSnapshot());
        statement.bindString(6, entity.getStartTimeSnapshot());
        statement.bindString(7, entity.getEndTimeSnapshot());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getOrderIndex());
      }
    };
    this.__preparedStmtOfUpdateCompletion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE daily_task_records\n"
                + "        SET isCompleted = ?\n"
                + "        WHERE date = ? AND templateId = ?\n"
                + "        ";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByDateAndTemplateId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_task_records WHERE date = ? AND templateId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_task_records";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final DailyTaskRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyTaskRecordEntity.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<DailyTaskRecordEntity> records,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyTaskRecordEntity.insert(records);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCompletion(final String date, final long templateId,
      final boolean isCompleted, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCompletion.acquire();
        int _argIndex = 1;
        final int _tmp = isCompleted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, date);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, templateId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateCompletion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByDateAndTemplateId(final String date, final long templateId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByDateAndTemplateId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, templateId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByDateAndTemplateId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyTaskRecordEntity>> observeByDate(final String date) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_task_records\n"
            + "        WHERE date = ?\n"
            + "        ORDER BY isCompleted ASC, orderIndex ASC, templateId ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_task_records"}, new Callable<List<DailyTaskRecordEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTemplateId = CursorUtil.getColumnIndexOrThrow(_cursor, "templateId");
          final int _cursorIndexOfTitleSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "titleSnapshot");
          final int _cursorIndexOfNoteSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "noteSnapshot");
          final int _cursorIndexOfDurationTextSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "durationTextSnapshot");
          final int _cursorIndexOfStartTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimeSnapshot");
          final int _cursorIndexOfEndTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimeSnapshot");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<DailyTaskRecordEntity> _result = new ArrayList<DailyTaskRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskRecordEntity _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTemplateId;
            _tmpTemplateId = _cursor.getLong(_cursorIndexOfTemplateId);
            final String _tmpTitleSnapshot;
            _tmpTitleSnapshot = _cursor.getString(_cursorIndexOfTitleSnapshot);
            final String _tmpNoteSnapshot;
            _tmpNoteSnapshot = _cursor.getString(_cursorIndexOfNoteSnapshot);
            final String _tmpDurationTextSnapshot;
            _tmpDurationTextSnapshot = _cursor.getString(_cursorIndexOfDurationTextSnapshot);
            final String _tmpStartTimeSnapshot;
            _tmpStartTimeSnapshot = _cursor.getString(_cursorIndexOfStartTimeSnapshot);
            final String _tmpEndTimeSnapshot;
            _tmpEndTimeSnapshot = _cursor.getString(_cursorIndexOfEndTimeSnapshot);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new DailyTaskRecordEntity(_tmpDate,_tmpTemplateId,_tmpTitleSnapshot,_tmpNoteSnapshot,_tmpDurationTextSnapshot,_tmpStartTimeSnapshot,_tmpEndTimeSnapshot,_tmpIsCompleted,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByDate(final String date,
      final Continuation<? super List<DailyTaskRecordEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_task_records\n"
            + "        WHERE date = ?\n"
            + "        ORDER BY orderIndex ASC, templateId ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyTaskRecordEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTemplateId = CursorUtil.getColumnIndexOrThrow(_cursor, "templateId");
          final int _cursorIndexOfTitleSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "titleSnapshot");
          final int _cursorIndexOfNoteSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "noteSnapshot");
          final int _cursorIndexOfDurationTextSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "durationTextSnapshot");
          final int _cursorIndexOfStartTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimeSnapshot");
          final int _cursorIndexOfEndTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimeSnapshot");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<DailyTaskRecordEntity> _result = new ArrayList<DailyTaskRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskRecordEntity _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTemplateId;
            _tmpTemplateId = _cursor.getLong(_cursorIndexOfTemplateId);
            final String _tmpTitleSnapshot;
            _tmpTitleSnapshot = _cursor.getString(_cursorIndexOfTitleSnapshot);
            final String _tmpNoteSnapshot;
            _tmpNoteSnapshot = _cursor.getString(_cursorIndexOfNoteSnapshot);
            final String _tmpDurationTextSnapshot;
            _tmpDurationTextSnapshot = _cursor.getString(_cursorIndexOfDurationTextSnapshot);
            final String _tmpStartTimeSnapshot;
            _tmpStartTimeSnapshot = _cursor.getString(_cursorIndexOfStartTimeSnapshot);
            final String _tmpEndTimeSnapshot;
            _tmpEndTimeSnapshot = _cursor.getString(_cursorIndexOfEndTimeSnapshot);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new DailyTaskRecordEntity(_tmpDate,_tmpTemplateId,_tmpTitleSnapshot,_tmpNoteSnapshot,_tmpDurationTextSnapshot,_tmpStartTimeSnapshot,_tmpEndTimeSnapshot,_tmpIsCompleted,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecord(final String date, final long templateId,
      final Continuation<? super DailyTaskRecordEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_task_records\n"
            + "        WHERE date = ? AND templateId = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    _argIndex = 2;
    _statement.bindLong(_argIndex, templateId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyTaskRecordEntity>() {
      @Override
      @Nullable
      public DailyTaskRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTemplateId = CursorUtil.getColumnIndexOrThrow(_cursor, "templateId");
          final int _cursorIndexOfTitleSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "titleSnapshot");
          final int _cursorIndexOfNoteSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "noteSnapshot");
          final int _cursorIndexOfDurationTextSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "durationTextSnapshot");
          final int _cursorIndexOfStartTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimeSnapshot");
          final int _cursorIndexOfEndTimeSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimeSnapshot");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final DailyTaskRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTemplateId;
            _tmpTemplateId = _cursor.getLong(_cursorIndexOfTemplateId);
            final String _tmpTitleSnapshot;
            _tmpTitleSnapshot = _cursor.getString(_cursorIndexOfTitleSnapshot);
            final String _tmpNoteSnapshot;
            _tmpNoteSnapshot = _cursor.getString(_cursorIndexOfNoteSnapshot);
            final String _tmpDurationTextSnapshot;
            _tmpDurationTextSnapshot = _cursor.getString(_cursorIndexOfDurationTextSnapshot);
            final String _tmpStartTimeSnapshot;
            _tmpStartTimeSnapshot = _cursor.getString(_cursorIndexOfStartTimeSnapshot);
            final String _tmpEndTimeSnapshot;
            _tmpEndTimeSnapshot = _cursor.getString(_cursorIndexOfEndTimeSnapshot);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _result = new DailyTaskRecordEntity(_tmpDate,_tmpTemplateId,_tmpTitleSnapshot,_tmpNoteSnapshot,_tmpDurationTextSnapshot,_tmpStartTimeSnapshot,_tmpEndTimeSnapshot,_tmpIsCompleted,_tmpOrderIndex);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyStatusSummaryEntity>> observeSummariesBetween(final String startDate,
      final String endDate) {
    final String _sql = "\n"
            + "        SELECT date, COUNT(*) AS totalCount, SUM(CASE WHEN isCompleted THEN 1 ELSE 0 END) AS completedCount\n"
            + "        FROM daily_task_records\n"
            + "        WHERE date BETWEEN ? AND ?\n"
            + "        GROUP BY date\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_task_records"}, new Callable<List<DailyStatusSummaryEntity>>() {
      @Override
      @NonNull
      public List<DailyStatusSummaryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfTotalCount = 1;
          final int _cursorIndexOfCompletedCount = 2;
          final List<DailyStatusSummaryEntity> _result = new ArrayList<DailyStatusSummaryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatusSummaryEntity _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpCompletedCount;
            _tmpCompletedCount = _cursor.getInt(_cursorIndexOfCompletedCount);
            _item = new DailyStatusSummaryEntity(_tmpDate,_tmpTotalCount,_tmpCompletedCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object deleteByDateAndTemplateIds(final String date, final List<Long> templateIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM daily_task_records WHERE date = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" AND templateId IN (");
        final int _inputSize = templateIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
        _argIndex = 2;
        for (long _item : templateIds) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
