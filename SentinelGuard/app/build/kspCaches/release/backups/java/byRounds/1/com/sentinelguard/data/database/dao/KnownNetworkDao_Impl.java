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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sentinelguard.data.database.entities.KnownNetworkEntity;
import java.lang.Boolean;
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
public final class KnownNetworkDao_Impl implements KnownNetworkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<KnownNetworkEntity> __insertionAdapterOfKnownNetworkEntity;

  private final EntityDeletionOrUpdateAdapter<KnownNetworkEntity> __deletionAdapterOfKnownNetworkEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementConnection;

  private final SharedSQLiteStatement __preparedStmtOfSetTrusted;

  public KnownNetworkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfKnownNetworkEntity = new EntityInsertionAdapter<KnownNetworkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `known_networks` (`ssid`,`bssid`,`isSecure`,`isTrusted`,`connectionCount`,`lastConnected`,`totalTimeConnectedMs`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final KnownNetworkEntity entity) {
        statement.bindString(1, entity.getSsid());
        if (entity.getBssid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getBssid());
        }
        final int _tmp = entity.isSecure() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.isTrusted() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        statement.bindLong(5, entity.getConnectionCount());
        statement.bindLong(6, entity.getLastConnected());
        statement.bindLong(7, entity.getTotalTimeConnectedMs());
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfKnownNetworkEntity = new EntityDeletionOrUpdateAdapter<KnownNetworkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `known_networks` WHERE `ssid` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final KnownNetworkEntity entity) {
        statement.bindString(1, entity.getSsid());
      }
    };
    this.__preparedStmtOfIncrementConnection = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE known_networks SET connectionCount = connectionCount + 1, lastConnected = ? WHERE ssid = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetTrusted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE known_networks SET isTrusted = ? WHERE ssid = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final KnownNetworkEntity network,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfKnownNetworkEntity.insert(network);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final KnownNetworkEntity network,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfKnownNetworkEntity.handle(network);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementConnection(final String ssid, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementConnection.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, ssid);
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
          __preparedStmtOfIncrementConnection.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setTrusted(final String ssid, final boolean trusted,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetTrusted.acquire();
        int _argIndex = 1;
        final int _tmp = trusted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, ssid);
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
          __preparedStmtOfSetTrusted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getNetwork(final String ssid,
      final Continuation<? super KnownNetworkEntity> $completion) {
    final String _sql = "SELECT * FROM known_networks WHERE ssid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ssid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<KnownNetworkEntity>() {
      @Override
      @Nullable
      public KnownNetworkEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "ssid");
          final int _cursorIndexOfBssid = CursorUtil.getColumnIndexOrThrow(_cursor, "bssid");
          final int _cursorIndexOfIsSecure = CursorUtil.getColumnIndexOrThrow(_cursor, "isSecure");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfConnectionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionCount");
          final int _cursorIndexOfLastConnected = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnected");
          final int _cursorIndexOfTotalTimeConnectedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeConnectedMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final KnownNetworkEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpSsid;
            _tmpSsid = _cursor.getString(_cursorIndexOfSsid);
            final String _tmpBssid;
            if (_cursor.isNull(_cursorIndexOfBssid)) {
              _tmpBssid = null;
            } else {
              _tmpBssid = _cursor.getString(_cursorIndexOfBssid);
            }
            final boolean _tmpIsSecure;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSecure);
            _tmpIsSecure = _tmp != 0;
            final boolean _tmpIsTrusted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp_1 != 0;
            final int _tmpConnectionCount;
            _tmpConnectionCount = _cursor.getInt(_cursorIndexOfConnectionCount);
            final long _tmpLastConnected;
            _tmpLastConnected = _cursor.getLong(_cursorIndexOfLastConnected);
            final long _tmpTotalTimeConnectedMs;
            _tmpTotalTimeConnectedMs = _cursor.getLong(_cursorIndexOfTotalTimeConnectedMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new KnownNetworkEntity(_tmpSsid,_tmpBssid,_tmpIsSecure,_tmpIsTrusted,_tmpConnectionCount,_tmpLastConnected,_tmpTotalTimeConnectedMs,_tmpCreatedAt);
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
  public Object getTrustedNetworks(
      final Continuation<? super List<KnownNetworkEntity>> $completion) {
    final String _sql = "SELECT * FROM known_networks WHERE isTrusted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<KnownNetworkEntity>>() {
      @Override
      @NonNull
      public List<KnownNetworkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "ssid");
          final int _cursorIndexOfBssid = CursorUtil.getColumnIndexOrThrow(_cursor, "bssid");
          final int _cursorIndexOfIsSecure = CursorUtil.getColumnIndexOrThrow(_cursor, "isSecure");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfConnectionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionCount");
          final int _cursorIndexOfLastConnected = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnected");
          final int _cursorIndexOfTotalTimeConnectedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeConnectedMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<KnownNetworkEntity> _result = new ArrayList<KnownNetworkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final KnownNetworkEntity _item;
            final String _tmpSsid;
            _tmpSsid = _cursor.getString(_cursorIndexOfSsid);
            final String _tmpBssid;
            if (_cursor.isNull(_cursorIndexOfBssid)) {
              _tmpBssid = null;
            } else {
              _tmpBssid = _cursor.getString(_cursorIndexOfBssid);
            }
            final boolean _tmpIsSecure;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSecure);
            _tmpIsSecure = _tmp != 0;
            final boolean _tmpIsTrusted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp_1 != 0;
            final int _tmpConnectionCount;
            _tmpConnectionCount = _cursor.getInt(_cursorIndexOfConnectionCount);
            final long _tmpLastConnected;
            _tmpLastConnected = _cursor.getLong(_cursorIndexOfLastConnected);
            final long _tmpTotalTimeConnectedMs;
            _tmpTotalTimeConnectedMs = _cursor.getLong(_cursorIndexOfTotalTimeConnectedMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new KnownNetworkEntity(_tmpSsid,_tmpBssid,_tmpIsSecure,_tmpIsTrusted,_tmpConnectionCount,_tmpLastConnected,_tmpTotalTimeConnectedMs,_tmpCreatedAt);
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
  public Object getAllNetworks(final Continuation<? super List<KnownNetworkEntity>> $completion) {
    final String _sql = "SELECT * FROM known_networks ORDER BY connectionCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<KnownNetworkEntity>>() {
      @Override
      @NonNull
      public List<KnownNetworkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "ssid");
          final int _cursorIndexOfBssid = CursorUtil.getColumnIndexOrThrow(_cursor, "bssid");
          final int _cursorIndexOfIsSecure = CursorUtil.getColumnIndexOrThrow(_cursor, "isSecure");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfConnectionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionCount");
          final int _cursorIndexOfLastConnected = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnected");
          final int _cursorIndexOfTotalTimeConnectedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeConnectedMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<KnownNetworkEntity> _result = new ArrayList<KnownNetworkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final KnownNetworkEntity _item;
            final String _tmpSsid;
            _tmpSsid = _cursor.getString(_cursorIndexOfSsid);
            final String _tmpBssid;
            if (_cursor.isNull(_cursorIndexOfBssid)) {
              _tmpBssid = null;
            } else {
              _tmpBssid = _cursor.getString(_cursorIndexOfBssid);
            }
            final boolean _tmpIsSecure;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSecure);
            _tmpIsSecure = _tmp != 0;
            final boolean _tmpIsTrusted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp_1 != 0;
            final int _tmpConnectionCount;
            _tmpConnectionCount = _cursor.getInt(_cursorIndexOfConnectionCount);
            final long _tmpLastConnected;
            _tmpLastConnected = _cursor.getLong(_cursorIndexOfLastConnected);
            final long _tmpTotalTimeConnectedMs;
            _tmpTotalTimeConnectedMs = _cursor.getLong(_cursorIndexOfTotalTimeConnectedMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new KnownNetworkEntity(_tmpSsid,_tmpBssid,_tmpIsSecure,_tmpIsTrusted,_tmpConnectionCount,_tmpLastConnected,_tmpTotalTimeConnectedMs,_tmpCreatedAt);
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
  public Object isKnownNetwork(final String ssid, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM known_networks WHERE ssid = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ssid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
