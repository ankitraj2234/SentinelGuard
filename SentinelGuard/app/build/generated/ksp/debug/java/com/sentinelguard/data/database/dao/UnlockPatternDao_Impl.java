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
import com.sentinelguard.data.database.entities.UnlockPatternEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
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
public final class UnlockPatternDao_Impl implements UnlockPatternDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UnlockPatternEntity> __insertionAdapterOfUnlockPatternEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementUnlock;

  private final SharedSQLiteStatement __preparedStmtOfIncrementFailedAttempt;

  public UnlockPatternDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUnlockPatternEntity = new EntityInsertionAdapter<UnlockPatternEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `unlock_patterns` (`id`,`hourOfDay`,`dayOfWeek`,`unlockCount`,`failedAttempts`,`avgSessionLengthMs`,`lastUpdated`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UnlockPatternEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getHourOfDay());
        statement.bindLong(3, entity.getDayOfWeek());
        statement.bindLong(4, entity.getUnlockCount());
        statement.bindLong(5, entity.getFailedAttempts());
        statement.bindLong(6, entity.getAvgSessionLengthMs());
        statement.bindLong(7, entity.getLastUpdated());
      }
    };
    this.__preparedStmtOfIncrementUnlock = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE unlock_patterns SET unlockCount = unlockCount + 1, lastUpdated = ? WHERE hourOfDay = ? AND dayOfWeek = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementFailedAttempt = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE unlock_patterns SET failedAttempts = failedAttempts + 1 WHERE hourOfDay = ? AND dayOfWeek = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final UnlockPatternEntity pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUnlockPatternEntity.insert(pattern);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementUnlock(final int hour, final int dayOfWeek, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementUnlock.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, hour);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, dayOfWeek);
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
          __preparedStmtOfIncrementUnlock.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementFailedAttempt(final int hour, final int dayOfWeek,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementFailedAttempt.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, hour);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, dayOfWeek);
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
          __preparedStmtOfIncrementFailedAttempt.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPattern(final int hour, final int dayOfWeek,
      final Continuation<? super UnlockPatternEntity> $completion) {
    final String _sql = "SELECT * FROM unlock_patterns WHERE hourOfDay = ? AND dayOfWeek = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayOfWeek);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UnlockPatternEntity>() {
      @Override
      @Nullable
      public UnlockPatternEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hourOfDay");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfUnlockCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockCount");
          final int _cursorIndexOfFailedAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedAttempts");
          final int _cursorIndexOfAvgSessionLengthMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSessionLengthMs");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final UnlockPatternEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpUnlockCount;
            _tmpUnlockCount = _cursor.getInt(_cursorIndexOfUnlockCount);
            final int _tmpFailedAttempts;
            _tmpFailedAttempts = _cursor.getInt(_cursorIndexOfFailedAttempts);
            final long _tmpAvgSessionLengthMs;
            _tmpAvgSessionLengthMs = _cursor.getLong(_cursorIndexOfAvgSessionLengthMs);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new UnlockPatternEntity(_tmpId,_tmpHourOfDay,_tmpDayOfWeek,_tmpUnlockCount,_tmpFailedAttempts,_tmpAvgSessionLengthMs,_tmpLastUpdated);
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
      final Continuation<? super List<UnlockPatternEntity>> $completion) {
    final String _sql = "SELECT * FROM unlock_patterns WHERE hourOfDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<UnlockPatternEntity>>() {
      @Override
      @NonNull
      public List<UnlockPatternEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHourOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "hourOfDay");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfUnlockCount = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockCount");
          final int _cursorIndexOfFailedAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedAttempts");
          final int _cursorIndexOfAvgSessionLengthMs = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSessionLengthMs");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final List<UnlockPatternEntity> _result = new ArrayList<UnlockPatternEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UnlockPatternEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpHourOfDay;
            _tmpHourOfDay = _cursor.getInt(_cursorIndexOfHourOfDay);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpUnlockCount;
            _tmpUnlockCount = _cursor.getInt(_cursorIndexOfUnlockCount);
            final int _tmpFailedAttempts;
            _tmpFailedAttempts = _cursor.getInt(_cursorIndexOfFailedAttempts);
            final long _tmpAvgSessionLengthMs;
            _tmpAvgSessionLengthMs = _cursor.getLong(_cursorIndexOfAvgSessionLengthMs);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _item = new UnlockPatternEntity(_tmpId,_tmpHourOfDay,_tmpDayOfWeek,_tmpUnlockCount,_tmpFailedAttempts,_tmpAvgSessionLengthMs,_tmpLastUpdated);
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
  public Object getAverageUnlocksForHour(final int hour,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(unlockCount) FROM unlock_patterns WHERE hourOfDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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
  public Object getTotalUnlocks(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(unlockCount) FROM unlock_patterns";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
