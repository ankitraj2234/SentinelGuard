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
import com.sentinelguard.data.database.entities.BehavioralAnomalyEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class BehavioralAnomalyDao_Impl implements BehavioralAnomalyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BehavioralAnomalyEntity> __insertionAdapterOfBehavioralAnomalyEntity;

  private final SharedSQLiteStatement __preparedStmtOfResolveAnomaly;

  private final SharedSQLiteStatement __preparedStmtOfResolveOldAnomalies;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldAnomalies;

  public BehavioralAnomalyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBehavioralAnomalyEntity = new EntityInsertionAdapter<BehavioralAnomalyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `behavioral_anomalies` (`id`,`anomalyType`,`description`,`severity`,`riskPoints`,`resolved`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BehavioralAnomalyEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getAnomalyType());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getSeverity());
        statement.bindLong(5, entity.getRiskPoints());
        final int _tmp = entity.getResolved() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfResolveAnomaly = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE behavioral_anomalies SET resolved = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResolveOldAnomalies = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE behavioral_anomalies SET resolved = 1 WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldAnomalies = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM behavioral_anomalies WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BehavioralAnomalyEntity anomaly,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBehavioralAnomalyEntity.insertAndReturnId(anomaly);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object resolveAnomaly(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResolveAnomaly.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfResolveAnomaly.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resolveOldAnomalies(final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResolveOldAnomalies.acquire();
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
          __preparedStmtOfResolveOldAnomalies.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldAnomalies(final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldAnomalies.acquire();
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
          __preparedStmtOfDeleteOldAnomalies.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnresolvedAnomalies(
      final Continuation<? super List<BehavioralAnomalyEntity>> $completion) {
    final String _sql = "SELECT * FROM behavioral_anomalies WHERE resolved = 0 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BehavioralAnomalyEntity>>() {
      @Override
      @NonNull
      public List<BehavioralAnomalyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAnomalyType = CursorUtil.getColumnIndexOrThrow(_cursor, "anomalyType");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "riskPoints");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<BehavioralAnomalyEntity> _result = new ArrayList<BehavioralAnomalyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralAnomalyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAnomalyType;
            _tmpAnomalyType = _cursor.getString(_cursorIndexOfAnomalyType);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpSeverity;
            _tmpSeverity = _cursor.getInt(_cursorIndexOfSeverity);
            final int _tmpRiskPoints;
            _tmpRiskPoints = _cursor.getInt(_cursorIndexOfRiskPoints);
            final boolean _tmpResolved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new BehavioralAnomalyEntity(_tmpId,_tmpAnomalyType,_tmpDescription,_tmpSeverity,_tmpRiskPoints,_tmpResolved,_tmpTimestamp);
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
  public Object getRecentAnomalies(final long since,
      final Continuation<? super List<BehavioralAnomalyEntity>> $completion) {
    final String _sql = "SELECT * FROM behavioral_anomalies WHERE timestamp > ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BehavioralAnomalyEntity>>() {
      @Override
      @NonNull
      public List<BehavioralAnomalyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAnomalyType = CursorUtil.getColumnIndexOrThrow(_cursor, "anomalyType");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "riskPoints");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<BehavioralAnomalyEntity> _result = new ArrayList<BehavioralAnomalyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralAnomalyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpAnomalyType;
            _tmpAnomalyType = _cursor.getString(_cursorIndexOfAnomalyType);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpSeverity;
            _tmpSeverity = _cursor.getInt(_cursorIndexOfSeverity);
            final int _tmpRiskPoints;
            _tmpRiskPoints = _cursor.getInt(_cursorIndexOfRiskPoints);
            final boolean _tmpResolved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new BehavioralAnomalyEntity(_tmpId,_tmpAnomalyType,_tmpDescription,_tmpSeverity,_tmpRiskPoints,_tmpResolved,_tmpTimestamp);
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
  public Object getTotalRiskPoints(final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(riskPoints) FROM behavioral_anomalies WHERE resolved = 0 AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
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
