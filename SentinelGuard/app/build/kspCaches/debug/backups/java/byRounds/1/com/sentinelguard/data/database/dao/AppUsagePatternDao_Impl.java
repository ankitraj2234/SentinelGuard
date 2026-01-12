package com.sentinelguard.data.database.dao;

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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sentinelguard.data.database.entities.AppUsagePatternEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppUsagePatternDao_Impl implements AppUsagePatternDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppUsagePatternEntity> __insertionAdapterOfAppUsagePatternEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementUsage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldPatterns;

  public AppUsagePatternDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppUsagePatternEntity = new EntityInsertionAdapter<AppUsagePatternEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_usage_patterns` (`id`,`packageName`,`hourOfDay`,`dayOfWeek`,`usageCount`,`avgDurationMs`,`lastUsed`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppUsagePatternEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindLong(3, entity.getHourOfDay());
        statement.bindLong(4, entity.getDayOfWeek());
        statement.bindLong(5, entity.getUsageCount());
        statement.bindLong(6, entity.getAvgDurationMs());
        statement.bindLong(7, entity.getLastUsed());
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfIncrementUsage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_usage_patterns SET usageCount = usageCount + 1, lastUsed = ? WHERE packageName = ? AND hourOfDay = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldPatterns = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM app_usage_patterns WHERE lastUsed < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AppUsagePatternEntity pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppUsagePatternEntity.insert(pattern);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementUsage(final String packageName, final int hour, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementUsage.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, hour);
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
          __preparedStmtOfIncrementUsage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldPatterns(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldPatterns.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
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
          __preparedStmtOfDeleteOldPatterns.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPattern(final String packageName, final int hour,
      final Continuation<? super AppUsagePatternEntity> $completion) {
    final String _sql = "SELECT * FROM app_usage_patterns WHERE packageName = ? AND hourOfDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    _argIndex = 2;
    _statement.bindLong(_argIndex, hour);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppUsagePatternEntity>() {
      @Override
      @Nullable
      public AppUsagePatternEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hourOfDay");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final int _cursorIndexOfAvgDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgDurationMs");
          final int _cursorIndexOfLastUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final AppUsagePatternEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpAvgDurationMs;
            _tmpAvgDurationMs = _cursor.getLong(_cursorIndexOfAvgDurationMs);
            final long _tmpLastUsed;
            _tmpLastUsed = _cursor.getLong(_cursorIndexOfLastUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new AppUsagePatternEntity(_tmpId,_tmpPackageName,_tmpHourOfDay,_tmpDayOfWeek,_tmpUsageCount,_tmpAvgDurationMs,_tmpLastUsed,_tmpCreatedAt);
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
  public Object getPatternsByHour(final int hour,
      final Continuation<? super List<AppUsagePatternEntity>> $completion) {
    final String _sql = "SELECT * FROM app_usage_patterns WHERE hourOfDay = ? ORDER BY usageCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppUsagePatternEntity>>() {
      @Override
      @NonNull
      public List<AppUsagePatternEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hourOfDay");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final int _cursorIndexOfAvgDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgDurationMs");
          final int _cursorIndexOfLastUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AppUsagePatternEntity> _result = new ArrayList<AppUsagePatternEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppUsagePatternEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpAvgDurationMs;
            _tmpAvgDurationMs = _cursor.getLong(_cursorIndexOfAvgDurationMs);
            final long _tmpLastUsed;
            _tmpLastUsed = _cursor.getLong(_cursorIndexOfLastUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AppUsagePatternEntity(_tmpId,_tmpPackageName,_tmpHourOfDay,_tmpDayOfWeek,_tmpUsageCount,_tmpAvgDurationMs,_tmpLastUsed,_tmpCreatedAt);
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
  public Object getPatternsByApp(final String packageName,
      final Continuation<? super List<AppUsagePatternEntity>> $completion) {
    final String _sql = "SELECT * FROM app_usage_patterns WHERE packageName = ? ORDER BY hourOfDay";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppUsagePatternEntity>>() {
      @Override
      @NonNull
      public List<AppUsagePatternEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hourOfDay");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final int _cursorIndexOfAvgDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgDurationMs");
          final int _cursorIndexOfLastUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AppUsagePatternEntity> _result = new ArrayList<AppUsagePatternEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppUsagePatternEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpAvgDurationMs;
            _tmpAvgDurationMs = _cursor.getLong(_cursorIndexOfAvgDurationMs);
            final long _tmpLastUsed;
            _tmpLastUsed = _cursor.getLong(_cursorIndexOfLastUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AppUsagePatternEntity(_tmpId,_tmpPackageName,_tmpHourOfDay,_tmpDayOfWeek,_tmpUsageCount,_tmpAvgDurationMs,_tmpLastUsed,_tmpCreatedAt);
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
  public Object getTypicalAppsForHour(final int hour, final int minCount,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT packageName FROM app_usage_patterns WHERE hourOfDay = ? AND usageCount >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    _argIndex = 2;
    _statement.bindLong(_argIndex, minCount);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
