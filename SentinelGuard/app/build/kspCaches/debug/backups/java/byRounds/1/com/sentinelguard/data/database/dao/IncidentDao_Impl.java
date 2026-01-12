package com.sentinelguard.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sentinelguard.data.database.Converters;
import com.sentinelguard.data.database.entities.IncidentEntity;
import com.sentinelguard.data.database.entities.IncidentSeverity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class IncidentDao_Impl implements IncidentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncidentEntity> __insertionAdapterOfIncidentEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<IncidentEntity> __updateAdapterOfIncidentEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkResolved;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public IncidentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncidentEntity = new EntityInsertionAdapter<IncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `incidents` (`id`,`severity`,`riskScore`,`triggeredBy`,`actionsTaken`,`summary`,`location`,`deviceState`,`timestamp`,`resolved`,`resolvedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncidentEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromIncidentSeverity(entity.getSeverity());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getRiskScore());
        statement.bindString(4, entity.getTriggeredBy());
        statement.bindString(5, entity.getActionsTaken());
        statement.bindString(6, entity.getSummary());
        if (entity.getLocation() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLocation());
        }
        if (entity.getDeviceState() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getDeviceState());
        }
        statement.bindLong(9, entity.getTimestamp());
        final int _tmp_1 = entity.getResolved() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getResolvedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getResolvedAt());
        }
      }
    };
    this.__updateAdapterOfIncidentEntity = new EntityDeletionOrUpdateAdapter<IncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `incidents` SET `id` = ?,`severity` = ?,`riskScore` = ?,`triggeredBy` = ?,`actionsTaken` = ?,`summary` = ?,`location` = ?,`deviceState` = ?,`timestamp` = ?,`resolved` = ?,`resolvedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncidentEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromIncidentSeverity(entity.getSeverity());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getRiskScore());
        statement.bindString(4, entity.getTriggeredBy());
        statement.bindString(5, entity.getActionsTaken());
        statement.bindString(6, entity.getSummary());
        if (entity.getLocation() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLocation());
        }
        if (entity.getDeviceState() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getDeviceState());
        }
        statement.bindLong(9, entity.getTimestamp());
        final int _tmp_1 = entity.getResolved() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getResolvedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getResolvedAt());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfMarkResolved = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE incidents SET resolved = 1, resolvedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incidents WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incidents";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IncidentEntity incident,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncidentEntity.insertAndReturnId(incident);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final IncidentEntity incident,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncidentEntity.handle(incident);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markResolved(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkResolved.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
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
          __preparedStmtOfMarkResolved.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
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
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super IncidentEntity> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncidentEntity>() {
      @Override
      @Nullable
      public IncidentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final IncidentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _result = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Object getRecent(final int limit,
      final Continuation<? super List<IncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM incidents ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Flow<List<IncidentEntity>> observeAll() {
    final String _sql = "SELECT * FROM incidents ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incidents"}, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Flow<List<IncidentEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM incidents ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incidents"}, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Object getBySeverity(final IncidentSeverity severity,
      final Continuation<? super List<IncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE severity = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromIncidentSeverity(severity);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp_1);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_2 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Flow<List<IncidentEntity>> observeBySeverities(
      final List<? extends IncidentSeverity> severities) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM incidents WHERE severity IN (");
    final int _inputSize = severities.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ORDER BY timestamp DESC");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (IncidentSeverity _item : severities) {
      final String _tmp = __converters.fromIncidentSeverity(_item);
      _statement.bindString(_argIndex, _tmp);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incidents"}, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp_1);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_2 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item_1 = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
            _result.add(_item_1);
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
  public Object getInRange(final long start, final long end,
      final Continuation<? super List<IncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Object getUnresolved(final Continuation<? super List<IncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE resolved = 0 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Object countBySeveritySince(final IncidentSeverity severity, final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM incidents WHERE severity = ? AND timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromIncidentSeverity(severity);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 2;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
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
  public Object getSince(final long since,
      final Continuation<? super List<IncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE timestamp >= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "riskScore");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfActionsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "actionsTaken");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfDeviceState = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceState");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "resolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final IncidentSeverity _tmpSeverity;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toIncidentSeverity(_tmp);
            final int _tmpRiskScore;
            _tmpRiskScore = _cursor.getInt(_cursorIndexOfRiskScore);
            final String _tmpTriggeredBy;
            _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            final String _tmpActionsTaken;
            _tmpActionsTaken = _cursor.getString(_cursorIndexOfActionsTaken);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpDeviceState;
            if (_cursor.isNull(_cursorIndexOfDeviceState)) {
              _tmpDeviceState = null;
            } else {
              _tmpDeviceState = _cursor.getString(_cursorIndexOfDeviceState);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfResolved);
            _tmpResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new IncidentEntity(_tmpId,_tmpSeverity,_tmpRiskScore,_tmpTriggeredBy,_tmpActionsTaken,_tmpSummary,_tmpLocation,_tmpDeviceState,_tmpTimestamp,_tmpResolved,_tmpResolvedAt);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM incidents";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
