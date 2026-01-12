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
import com.sentinelguard.data.database.entities.LocationClusterEntity;
import java.lang.Class;
import java.lang.Exception;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LocationClusterDao_Impl implements LocationClusterDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LocationClusterEntity> __insertionAdapterOfLocationClusterEntity;

  private final EntityDeletionOrUpdateAdapter<LocationClusterEntity> __deletionAdapterOfLocationClusterEntity;

  private final EntityDeletionOrUpdateAdapter<LocationClusterEntity> __updateAdapterOfLocationClusterEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementVisit;

  private final SharedSQLiteStatement __preparedStmtOfAddTimeSpent;

  private final SharedSQLiteStatement __preparedStmtOfSetLabel;

  public LocationClusterDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLocationClusterEntity = new EntityInsertionAdapter<LocationClusterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `location_clusters` (`id`,`centerLatitude`,`centerLongitude`,`radiusMeters`,`label`,`visitCount`,`totalTimeSpentMs`,`lastVisited`,`isTrusted`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationClusterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getCenterLatitude());
        statement.bindDouble(3, entity.getCenterLongitude());
        statement.bindDouble(4, entity.getRadiusMeters());
        if (entity.getLabel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLabel());
        }
        statement.bindLong(6, entity.getVisitCount());
        statement.bindLong(7, entity.getTotalTimeSpentMs());
        statement.bindLong(8, entity.getLastVisited());
        final int _tmp = entity.isTrusted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfLocationClusterEntity = new EntityDeletionOrUpdateAdapter<LocationClusterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `location_clusters` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationClusterEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfLocationClusterEntity = new EntityDeletionOrUpdateAdapter<LocationClusterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `location_clusters` SET `id` = ?,`centerLatitude` = ?,`centerLongitude` = ?,`radiusMeters` = ?,`label` = ?,`visitCount` = ?,`totalTimeSpentMs` = ?,`lastVisited` = ?,`isTrusted` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationClusterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getCenterLatitude());
        statement.bindDouble(3, entity.getCenterLongitude());
        statement.bindDouble(4, entity.getRadiusMeters());
        if (entity.getLabel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLabel());
        }
        statement.bindLong(6, entity.getVisitCount());
        statement.bindLong(7, entity.getTotalTimeSpentMs());
        statement.bindLong(8, entity.getLastVisited());
        final int _tmp = entity.isTrusted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfIncrementVisit = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE location_clusters SET visitCount = visitCount + 1, lastVisited = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAddTimeSpent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE location_clusters SET totalTimeSpentMs = totalTimeSpentMs + ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetLabel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE location_clusters SET label = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final LocationClusterEntity cluster,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfLocationClusterEntity.insertAndReturnId(cluster);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final LocationClusterEntity cluster,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfLocationClusterEntity.handle(cluster);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final LocationClusterEntity cluster,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfLocationClusterEntity.handle(cluster);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementVisit(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementVisit.acquire();
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
          __preparedStmtOfIncrementVisit.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addTimeSpent(final long id, final long duration,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddTimeSpent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, duration);
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
          __preparedStmtOfAddTimeSpent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setLabel(final long id, final String label,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetLabel.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, label);
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
          __preparedStmtOfSetLabel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getTrustedClusters(
      final Continuation<? super List<LocationClusterEntity>> $completion) {
    final String _sql = "SELECT * FROM location_clusters WHERE isTrusted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationClusterEntity>>() {
      @Override
      @NonNull
      public List<LocationClusterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCenterLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLatitude");
          final int _cursorIndexOfCenterLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLongitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfTotalTimeSpentMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeSpentMs");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<LocationClusterEntity> _result = new ArrayList<LocationClusterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationClusterEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpCenterLatitude;
            _tmpCenterLatitude = _cursor.getDouble(_cursorIndexOfCenterLatitude);
            final double _tmpCenterLongitude;
            _tmpCenterLongitude = _cursor.getDouble(_cursorIndexOfCenterLongitude);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final long _tmpTotalTimeSpentMs;
            _tmpTotalTimeSpentMs = _cursor.getLong(_cursorIndexOfTotalTimeSpentMs);
            final long _tmpLastVisited;
            _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            final boolean _tmpIsTrusted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new LocationClusterEntity(_tmpId,_tmpCenterLatitude,_tmpCenterLongitude,_tmpRadiusMeters,_tmpLabel,_tmpVisitCount,_tmpTotalTimeSpentMs,_tmpLastVisited,_tmpIsTrusted,_tmpCreatedAt);
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
  public Object getAllClusters(
      final Continuation<? super List<LocationClusterEntity>> $completion) {
    final String _sql = "SELECT * FROM location_clusters ORDER BY visitCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationClusterEntity>>() {
      @Override
      @NonNull
      public List<LocationClusterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCenterLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLatitude");
          final int _cursorIndexOfCenterLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLongitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfTotalTimeSpentMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeSpentMs");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<LocationClusterEntity> _result = new ArrayList<LocationClusterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationClusterEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpCenterLatitude;
            _tmpCenterLatitude = _cursor.getDouble(_cursorIndexOfCenterLatitude);
            final double _tmpCenterLongitude;
            _tmpCenterLongitude = _cursor.getDouble(_cursorIndexOfCenterLongitude);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final long _tmpTotalTimeSpentMs;
            _tmpTotalTimeSpentMs = _cursor.getLong(_cursorIndexOfTotalTimeSpentMs);
            final long _tmpLastVisited;
            _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            final boolean _tmpIsTrusted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new LocationClusterEntity(_tmpId,_tmpCenterLatitude,_tmpCenterLongitude,_tmpRadiusMeters,_tmpLabel,_tmpVisitCount,_tmpTotalTimeSpentMs,_tmpLastVisited,_tmpIsTrusted,_tmpCreatedAt);
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
  public Flow<List<LocationClusterEntity>> observeAllClusters() {
    final String _sql = "SELECT * FROM location_clusters ORDER BY visitCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"location_clusters"}, new Callable<List<LocationClusterEntity>>() {
      @Override
      @NonNull
      public List<LocationClusterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCenterLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLatitude");
          final int _cursorIndexOfCenterLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLongitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfTotalTimeSpentMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeSpentMs");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<LocationClusterEntity> _result = new ArrayList<LocationClusterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationClusterEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpCenterLatitude;
            _tmpCenterLatitude = _cursor.getDouble(_cursorIndexOfCenterLatitude);
            final double _tmpCenterLongitude;
            _tmpCenterLongitude = _cursor.getDouble(_cursorIndexOfCenterLongitude);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final long _tmpTotalTimeSpentMs;
            _tmpTotalTimeSpentMs = _cursor.getLong(_cursorIndexOfTotalTimeSpentMs);
            final long _tmpLastVisited;
            _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            final boolean _tmpIsTrusted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new LocationClusterEntity(_tmpId,_tmpCenterLatitude,_tmpCenterLongitude,_tmpRadiusMeters,_tmpLabel,_tmpVisitCount,_tmpTotalTimeSpentMs,_tmpLastVisited,_tmpIsTrusted,_tmpCreatedAt);
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
  public Object getClusterById(final long id,
      final Continuation<? super LocationClusterEntity> $completion) {
    final String _sql = "SELECT * FROM location_clusters WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LocationClusterEntity>() {
      @Override
      @Nullable
      public LocationClusterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCenterLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLatitude");
          final int _cursorIndexOfCenterLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "centerLongitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfTotalTimeSpentMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTimeSpentMs");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final int _cursorIndexOfIsTrusted = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrusted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final LocationClusterEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpCenterLatitude;
            _tmpCenterLatitude = _cursor.getDouble(_cursorIndexOfCenterLatitude);
            final double _tmpCenterLongitude;
            _tmpCenterLongitude = _cursor.getDouble(_cursorIndexOfCenterLongitude);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final long _tmpTotalTimeSpentMs;
            _tmpTotalTimeSpentMs = _cursor.getLong(_cursorIndexOfTotalTimeSpentMs);
            final long _tmpLastVisited;
            _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            final boolean _tmpIsTrusted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrusted);
            _tmpIsTrusted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new LocationClusterEntity(_tmpId,_tmpCenterLatitude,_tmpCenterLongitude,_tmpRadiusMeters,_tmpLabel,_tmpVisitCount,_tmpTotalTimeSpentMs,_tmpLastVisited,_tmpIsTrusted,_tmpCreatedAt);
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
