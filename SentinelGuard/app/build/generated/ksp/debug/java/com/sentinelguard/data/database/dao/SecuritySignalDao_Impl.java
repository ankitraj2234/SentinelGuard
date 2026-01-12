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
import com.sentinelguard.data.database.entities.SecuritySignalEntity;
import com.sentinelguard.data.database.entities.SignalType;
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
public final class SecuritySignalDao_Impl implements SecuritySignalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SecuritySignalEntity> __insertionAdapterOfSecuritySignalEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SecuritySignalEntity> __updateAdapterOfSecuritySignalEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SecuritySignalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSecuritySignalEntity = new EntityInsertionAdapter<SecuritySignalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `security_signals` (`id`,`signalType`,`value`,`timestamp`,`metadata`,`processed`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SecuritySignalEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromSignalType(entity.getSignalType());
        statement.bindString(2, _tmp);
        if (entity.getValue() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getValue());
        }
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getMetadata() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMetadata());
        }
        final int _tmp_1 = entity.getProcessed() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__updateAdapterOfSecuritySignalEntity = new EntityDeletionOrUpdateAdapter<SecuritySignalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `security_signals` SET `id` = ?,`signalType` = ?,`value` = ?,`timestamp` = ?,`metadata` = ?,`processed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SecuritySignalEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromSignalType(entity.getSignalType());
        statement.bindString(2, _tmp);
        if (entity.getValue() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getValue());
        }
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getMetadata() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMetadata());
        }
        final int _tmp_1 = entity.getProcessed() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM security_signals WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM security_signals";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SecuritySignalEntity signal,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSecuritySignalEntity.insertAndReturnId(signal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<SecuritySignalEntity> signals,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSecuritySignalEntity.insert(signals);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SecuritySignalEntity signal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSecuritySignalEntity.handle(signal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Object getById(final long id,
      final Continuation<? super SecuritySignalEntity> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SecuritySignalEntity>() {
      @Override
      @Nullable
      public SecuritySignalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final SecuritySignalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _result = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Flow<List<SecuritySignalEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"security_signals"}, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object getByType(final SignalType type, final int limit,
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE signalType = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromSignalType(type);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp_1);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_2 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object getSince(final long since,
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE timestamp >= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object getInRange(final long start, final long end,
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object getByTypeSince(final SignalType type, final long since,
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE signalType = ? AND timestamp >= ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromSignalType(type);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 2;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp_1);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_2 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object getUnprocessed(final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final String _sql = "SELECT * FROM security_signals WHERE processed = 0 ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_1 != 0;
            _item = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
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
  public Object countByTypeSince(final SignalType type, final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM security_signals WHERE signalType = ? AND timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromSignalType(type);
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
  public Object getByTypesSince(final List<? extends SignalType> types, final long since,
      final Continuation<? super List<SecuritySignalEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM security_signals WHERE signalType IN (");
    final int _inputSize = types.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") AND timestamp >= ");
    _stringBuilder.append("?");
    _stringBuilder.append(" ORDER BY timestamp DESC");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (SignalType _item : types) {
      final String _tmp = __converters.fromSignalType(_item);
      _statement.bindString(_argIndex, _tmp);
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SecuritySignalEntity>>() {
      @Override
      @NonNull
      public List<SecuritySignalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSignalType = CursorUtil.getColumnIndexOrThrow(_cursor, "signalType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "processed");
          final List<SecuritySignalEntity> _result = new ArrayList<SecuritySignalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SecuritySignalEntity _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final SignalType _tmpSignalType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSignalType);
            _tmpSignalType = __converters.toSignalType(_tmp_1);
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpProcessed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfProcessed);
            _tmpProcessed = _tmp_2 != 0;
            _item_1 = new SecuritySignalEntity(_tmpId,_tmpSignalType,_tmpValue,_tmpTimestamp,_tmpMetadata,_tmpProcessed);
            _result.add(_item_1);
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
    final String _sql = "SELECT COUNT(*) FROM security_signals";
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

  @Override
  public Object markProcessed(final List<Long> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE security_signals SET processed = 1 WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
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
