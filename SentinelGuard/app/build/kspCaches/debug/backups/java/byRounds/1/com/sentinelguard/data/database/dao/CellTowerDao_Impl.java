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
import com.sentinelguard.data.database.entities.CellTowerCacheEntity;
import com.sentinelguard.data.database.entities.CellTowerHistoryEntity;
import com.sentinelguard.data.database.entities.CellTowerIncidentEntity;
import java.lang.Class;
import java.lang.Double;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CellTowerDao_Impl implements CellTowerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CellTowerCacheEntity> __insertionAdapterOfCellTowerCacheEntity;

  private final EntityInsertionAdapter<CellTowerHistoryEntity> __insertionAdapterOfCellTowerHistoryEntity;

  private final EntityInsertionAdapter<CellTowerIncidentEntity> __insertionAdapterOfCellTowerIncidentEntity;

  private final EntityDeletionOrUpdateAdapter<CellTowerHistoryEntity> __updateAdapterOfCellTowerHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<CellTowerIncidentEntity> __updateAdapterOfCellTowerIncidentEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpiredCache;

  private final SharedSQLiteStatement __preparedStmtOfCloseOpenConnections;

  private final SharedSQLiteStatement __preparedStmtOfClearAllHistory;

  private final SharedSQLiteStatement __preparedStmtOfMarkEmailSent;

  public CellTowerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCellTowerCacheEntity = new EntityInsertionAdapter<CellTowerCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cell_tower_cache` (`id`,`cellId`,`lac`,`mcc`,`mnc`,`radioType`,`latitude`,`longitude`,`accuracy`,`range`,`samples`,`areaName`,`towerType`,`securityStatus`,`cachedAt`,`expiresAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerCacheEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCellId());
        statement.bindString(3, entity.getLac());
        statement.bindString(4, entity.getMcc());
        statement.bindString(5, entity.getMnc());
        statement.bindString(6, entity.getRadioType());
        statement.bindDouble(7, entity.getLatitude());
        statement.bindDouble(8, entity.getLongitude());
        statement.bindLong(9, entity.getAccuracy());
        statement.bindLong(10, entity.getRange());
        statement.bindLong(11, entity.getSamples());
        if (entity.getAreaName() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getAreaName());
        }
        statement.bindString(13, entity.getTowerType());
        statement.bindString(14, entity.getSecurityStatus());
        statement.bindLong(15, entity.getCachedAt());
        statement.bindLong(16, entity.getExpiresAt());
      }
    };
    this.__insertionAdapterOfCellTowerHistoryEntity = new EntityInsertionAdapter<CellTowerHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `cell_tower_history` (`id`,`cellId`,`lac`,`mcc`,`mnc`,`latitude`,`longitude`,`areaName`,`carrierName`,`networkType`,`signalStrength`,`connectedAt`,`disconnectedAt`,`securityStatus`,`wasAlertSent`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCellId());
        statement.bindString(3, entity.getLac());
        if (entity.getMcc() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMcc());
        }
        if (entity.getMnc() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMnc());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getLongitude());
        }
        if (entity.getAreaName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAreaName());
        }
        if (entity.getCarrierName() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCarrierName());
        }
        if (entity.getNetworkType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNetworkType());
        }
        if (entity.getSignalStrength() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getSignalStrength());
        }
        statement.bindLong(12, entity.getConnectedAt());
        if (entity.getDisconnectedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getDisconnectedAt());
        }
        statement.bindString(14, entity.getSecurityStatus());
        final int _tmp = entity.getWasAlertSent() ? 1 : 0;
        statement.bindLong(15, _tmp);
      }
    };
    this.__insertionAdapterOfCellTowerIncidentEntity = new EntityInsertionAdapter<CellTowerIncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `cell_tower_incidents` (`id`,`cellId`,`lac`,`incidentType`,`riskLevel`,`description`,`indicators`,`latitude`,`longitude`,`areaName`,`occurredAt`,`wasEmailSent`,`wasResolved`,`resolvedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerIncidentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCellId());
        if (entity.getLac() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLac());
        }
        statement.bindString(4, entity.getIncidentType());
        statement.bindString(5, entity.getRiskLevel());
        statement.bindString(6, entity.getDescription());
        statement.bindString(7, entity.getIndicators());
        if (entity.getLatitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLongitude());
        }
        if (entity.getAreaName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAreaName());
        }
        statement.bindLong(11, entity.getOccurredAt());
        final int _tmp = entity.getWasEmailSent() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.getWasResolved() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        if (entity.getResolvedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getResolvedAt());
        }
      }
    };
    this.__updateAdapterOfCellTowerHistoryEntity = new EntityDeletionOrUpdateAdapter<CellTowerHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cell_tower_history` SET `id` = ?,`cellId` = ?,`lac` = ?,`mcc` = ?,`mnc` = ?,`latitude` = ?,`longitude` = ?,`areaName` = ?,`carrierName` = ?,`networkType` = ?,`signalStrength` = ?,`connectedAt` = ?,`disconnectedAt` = ?,`securityStatus` = ?,`wasAlertSent` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCellId());
        statement.bindString(3, entity.getLac());
        if (entity.getMcc() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMcc());
        }
        if (entity.getMnc() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMnc());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getLongitude());
        }
        if (entity.getAreaName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAreaName());
        }
        if (entity.getCarrierName() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCarrierName());
        }
        if (entity.getNetworkType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNetworkType());
        }
        if (entity.getSignalStrength() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getSignalStrength());
        }
        statement.bindLong(12, entity.getConnectedAt());
        if (entity.getDisconnectedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getDisconnectedAt());
        }
        statement.bindString(14, entity.getSecurityStatus());
        final int _tmp = entity.getWasAlertSent() ? 1 : 0;
        statement.bindLong(15, _tmp);
        statement.bindLong(16, entity.getId());
      }
    };
    this.__updateAdapterOfCellTowerIncidentEntity = new EntityDeletionOrUpdateAdapter<CellTowerIncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cell_tower_incidents` SET `id` = ?,`cellId` = ?,`lac` = ?,`incidentType` = ?,`riskLevel` = ?,`description` = ?,`indicators` = ?,`latitude` = ?,`longitude` = ?,`areaName` = ?,`occurredAt` = ?,`wasEmailSent` = ?,`wasResolved` = ?,`resolvedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerIncidentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCellId());
        if (entity.getLac() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLac());
        }
        statement.bindString(4, entity.getIncidentType());
        statement.bindString(5, entity.getRiskLevel());
        statement.bindString(6, entity.getDescription());
        statement.bindString(7, entity.getIndicators());
        if (entity.getLatitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLongitude());
        }
        if (entity.getAreaName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAreaName());
        }
        statement.bindLong(11, entity.getOccurredAt());
        final int _tmp = entity.getWasEmailSent() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.getWasResolved() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        if (entity.getResolvedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getResolvedAt());
        }
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteExpiredCache = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cell_tower_cache WHERE expiresAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfCloseOpenConnections = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE cell_tower_history \n"
                + "        SET disconnectedAt = ? \n"
                + "        WHERE disconnectedAt IS NULL\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cell_tower_history";
        return _query;
      }
    };
    this.__preparedStmtOfMarkEmailSent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE cell_tower_incidents SET wasEmailSent = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdateCache(final CellTowerCacheEntity tower,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCellTowerCacheEntity.insert(tower);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertHistory(final CellTowerHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCellTowerHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertIncident(final CellTowerIncidentEntity incident,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCellTowerIncidentEntity.insertAndReturnId(incident);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateHistory(final CellTowerHistoryEntity history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCellTowerHistoryEntity.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIncident(final CellTowerIncidentEntity incident,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCellTowerIncidentEntity.handle(incident);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpiredCache(final long currentTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpiredCache.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, currentTime);
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
          __preparedStmtOfDeleteExpiredCache.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object closeOpenConnections(final long disconnectedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCloseOpenConnections.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, disconnectedAt);
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
          __preparedStmtOfCloseOpenConnections.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllHistory.acquire();
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
          __preparedStmtOfClearAllHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markEmailSent(final long incidentId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkEmailSent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, incidentId);
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
          __preparedStmtOfMarkEmailSent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getCachedTower(final String cellId, final String lac, final String mcc,
      final String mnc, final long currentTime,
      final Continuation<? super CellTowerCacheEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM cell_tower_cache \n"
            + "        WHERE cellId = ? AND lac = ? AND mcc = ? AND mnc = ? \n"
            + "        AND expiresAt > ?\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 5);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cellId);
    _argIndex = 2;
    _statement.bindString(_argIndex, lac);
    _argIndex = 3;
    _statement.bindString(_argIndex, mcc);
    _argIndex = 4;
    _statement.bindString(_argIndex, mnc);
    _argIndex = 5;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CellTowerCacheEntity>() {
      @Override
      @Nullable
      public CellTowerCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfRadioType = CursorUtil.getColumnIndexOrThrow(_cursor, "radioType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfSamples = CursorUtil.getColumnIndexOrThrow(_cursor, "samples");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfTowerType = CursorUtil.getColumnIndexOrThrow(_cursor, "towerType");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final CellTowerCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            final String _tmpMnc;
            _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            final String _tmpRadioType;
            _tmpRadioType = _cursor.getString(_cursorIndexOfRadioType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpAccuracy;
            _tmpAccuracy = _cursor.getInt(_cursorIndexOfAccuracy);
            final int _tmpRange;
            _tmpRange = _cursor.getInt(_cursorIndexOfRange);
            final int _tmpSamples;
            _tmpSamples = _cursor.getInt(_cursorIndexOfSamples);
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpTowerType;
            _tmpTowerType = _cursor.getString(_cursorIndexOfTowerType);
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            _result = new CellTowerCacheEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpRadioType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpRange,_tmpSamples,_tmpAreaName,_tmpTowerType,_tmpSecurityStatus,_tmpCachedAt,_tmpExpiresAt);
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
  public Object getCurrentConnection(
      final Continuation<? super CellTowerHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM cell_tower_history WHERE disconnectedAt IS NULL ORDER BY connectedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CellTowerHistoryEntity>() {
      @Override
      @Nullable
      public CellTowerHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfCarrierName = CursorUtil.getColumnIndexOrThrow(_cursor, "carrierName");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "connectedAt");
          final int _cursorIndexOfDisconnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disconnectedAt");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfWasAlertSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAlertSent");
          final CellTowerHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            if (_cursor.isNull(_cursorIndexOfMcc)) {
              _tmpMcc = null;
            } else {
              _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            }
            final String _tmpMnc;
            if (_cursor.isNull(_cursorIndexOfMnc)) {
              _tmpMnc = null;
            } else {
              _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpCarrierName;
            if (_cursor.isNull(_cursorIndexOfCarrierName)) {
              _tmpCarrierName = null;
            } else {
              _tmpCarrierName = _cursor.getString(_cursorIndexOfCarrierName);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final long _tmpConnectedAt;
            _tmpConnectedAt = _cursor.getLong(_cursorIndexOfConnectedAt);
            final Long _tmpDisconnectedAt;
            if (_cursor.isNull(_cursorIndexOfDisconnectedAt)) {
              _tmpDisconnectedAt = null;
            } else {
              _tmpDisconnectedAt = _cursor.getLong(_cursorIndexOfDisconnectedAt);
            }
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final boolean _tmpWasAlertSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAlertSent);
            _tmpWasAlertSent = _tmp != 0;
            _result = new CellTowerHistoryEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpCarrierName,_tmpNetworkType,_tmpSignalStrength,_tmpConnectedAt,_tmpDisconnectedAt,_tmpSecurityStatus,_tmpWasAlertSent);
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
  public Object getRecentHistory(final int limit,
      final Continuation<? super List<CellTowerHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM cell_tower_history ORDER BY connectedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerHistoryEntity>>() {
      @Override
      @NonNull
      public List<CellTowerHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfCarrierName = CursorUtil.getColumnIndexOrThrow(_cursor, "carrierName");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "connectedAt");
          final int _cursorIndexOfDisconnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disconnectedAt");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfWasAlertSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAlertSent");
          final List<CellTowerHistoryEntity> _result = new ArrayList<CellTowerHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            if (_cursor.isNull(_cursorIndexOfMcc)) {
              _tmpMcc = null;
            } else {
              _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            }
            final String _tmpMnc;
            if (_cursor.isNull(_cursorIndexOfMnc)) {
              _tmpMnc = null;
            } else {
              _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpCarrierName;
            if (_cursor.isNull(_cursorIndexOfCarrierName)) {
              _tmpCarrierName = null;
            } else {
              _tmpCarrierName = _cursor.getString(_cursorIndexOfCarrierName);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final long _tmpConnectedAt;
            _tmpConnectedAt = _cursor.getLong(_cursorIndexOfConnectedAt);
            final Long _tmpDisconnectedAt;
            if (_cursor.isNull(_cursorIndexOfDisconnectedAt)) {
              _tmpDisconnectedAt = null;
            } else {
              _tmpDisconnectedAt = _cursor.getLong(_cursorIndexOfDisconnectedAt);
            }
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final boolean _tmpWasAlertSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAlertSent);
            _tmpWasAlertSent = _tmp != 0;
            _item = new CellTowerHistoryEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpCarrierName,_tmpNetworkType,_tmpSignalStrength,_tmpConnectedAt,_tmpDisconnectedAt,_tmpSecurityStatus,_tmpWasAlertSent);
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
  public Object getLastHistoryEntry(
      final Continuation<? super CellTowerHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM cell_tower_history ORDER BY connectedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CellTowerHistoryEntity>() {
      @Override
      @Nullable
      public CellTowerHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfCarrierName = CursorUtil.getColumnIndexOrThrow(_cursor, "carrierName");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "connectedAt");
          final int _cursorIndexOfDisconnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disconnectedAt");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfWasAlertSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAlertSent");
          final CellTowerHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            if (_cursor.isNull(_cursorIndexOfMcc)) {
              _tmpMcc = null;
            } else {
              _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            }
            final String _tmpMnc;
            if (_cursor.isNull(_cursorIndexOfMnc)) {
              _tmpMnc = null;
            } else {
              _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpCarrierName;
            if (_cursor.isNull(_cursorIndexOfCarrierName)) {
              _tmpCarrierName = null;
            } else {
              _tmpCarrierName = _cursor.getString(_cursorIndexOfCarrierName);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final long _tmpConnectedAt;
            _tmpConnectedAt = _cursor.getLong(_cursorIndexOfConnectedAt);
            final Long _tmpDisconnectedAt;
            if (_cursor.isNull(_cursorIndexOfDisconnectedAt)) {
              _tmpDisconnectedAt = null;
            } else {
              _tmpDisconnectedAt = _cursor.getLong(_cursorIndexOfDisconnectedAt);
            }
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final boolean _tmpWasAlertSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAlertSent);
            _tmpWasAlertSent = _tmp != 0;
            _result = new CellTowerHistoryEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpCarrierName,_tmpNetworkType,_tmpSignalStrength,_tmpConnectedAt,_tmpDisconnectedAt,_tmpSecurityStatus,_tmpWasAlertSent);
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
  public Object getHistorySince(final long startTime,
      final Continuation<? super List<CellTowerHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM cell_tower_history WHERE connectedAt >= ? ORDER BY connectedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerHistoryEntity>>() {
      @Override
      @NonNull
      public List<CellTowerHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfCarrierName = CursorUtil.getColumnIndexOrThrow(_cursor, "carrierName");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "connectedAt");
          final int _cursorIndexOfDisconnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disconnectedAt");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfWasAlertSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAlertSent");
          final List<CellTowerHistoryEntity> _result = new ArrayList<CellTowerHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            if (_cursor.isNull(_cursorIndexOfMcc)) {
              _tmpMcc = null;
            } else {
              _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            }
            final String _tmpMnc;
            if (_cursor.isNull(_cursorIndexOfMnc)) {
              _tmpMnc = null;
            } else {
              _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpCarrierName;
            if (_cursor.isNull(_cursorIndexOfCarrierName)) {
              _tmpCarrierName = null;
            } else {
              _tmpCarrierName = _cursor.getString(_cursorIndexOfCarrierName);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final long _tmpConnectedAt;
            _tmpConnectedAt = _cursor.getLong(_cursorIndexOfConnectedAt);
            final Long _tmpDisconnectedAt;
            if (_cursor.isNull(_cursorIndexOfDisconnectedAt)) {
              _tmpDisconnectedAt = null;
            } else {
              _tmpDisconnectedAt = _cursor.getLong(_cursorIndexOfDisconnectedAt);
            }
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final boolean _tmpWasAlertSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAlertSent);
            _tmpWasAlertSent = _tmp != 0;
            _item = new CellTowerHistoryEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpCarrierName,_tmpNetworkType,_tmpSignalStrength,_tmpConnectedAt,_tmpDisconnectedAt,_tmpSecurityStatus,_tmpWasAlertSent);
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
  public Flow<List<CellTowerHistoryEntity>> observeHistory() {
    final String _sql = "SELECT * FROM cell_tower_history ORDER BY connectedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cell_tower_history"}, new Callable<List<CellTowerHistoryEntity>>() {
      @Override
      @NonNull
      public List<CellTowerHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfCarrierName = CursorUtil.getColumnIndexOrThrow(_cursor, "carrierName");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "networkType");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "connectedAt");
          final int _cursorIndexOfDisconnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disconnectedAt");
          final int _cursorIndexOfSecurityStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "securityStatus");
          final int _cursorIndexOfWasAlertSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAlertSent");
          final List<CellTowerHistoryEntity> _result = new ArrayList<CellTowerHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            _tmpLac = _cursor.getString(_cursorIndexOfLac);
            final String _tmpMcc;
            if (_cursor.isNull(_cursorIndexOfMcc)) {
              _tmpMcc = null;
            } else {
              _tmpMcc = _cursor.getString(_cursorIndexOfMcc);
            }
            final String _tmpMnc;
            if (_cursor.isNull(_cursorIndexOfMnc)) {
              _tmpMnc = null;
            } else {
              _tmpMnc = _cursor.getString(_cursorIndexOfMnc);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final String _tmpCarrierName;
            if (_cursor.isNull(_cursorIndexOfCarrierName)) {
              _tmpCarrierName = null;
            } else {
              _tmpCarrierName = _cursor.getString(_cursorIndexOfCarrierName);
            }
            final String _tmpNetworkType;
            if (_cursor.isNull(_cursorIndexOfNetworkType)) {
              _tmpNetworkType = null;
            } else {
              _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final long _tmpConnectedAt;
            _tmpConnectedAt = _cursor.getLong(_cursorIndexOfConnectedAt);
            final Long _tmpDisconnectedAt;
            if (_cursor.isNull(_cursorIndexOfDisconnectedAt)) {
              _tmpDisconnectedAt = null;
            } else {
              _tmpDisconnectedAt = _cursor.getLong(_cursorIndexOfDisconnectedAt);
            }
            final String _tmpSecurityStatus;
            _tmpSecurityStatus = _cursor.getString(_cursorIndexOfSecurityStatus);
            final boolean _tmpWasAlertSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAlertSent);
            _tmpWasAlertSent = _tmp != 0;
            _item = new CellTowerHistoryEntity(_tmpId,_tmpCellId,_tmpLac,_tmpMcc,_tmpMnc,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpCarrierName,_tmpNetworkType,_tmpSignalStrength,_tmpConnectedAt,_tmpDisconnectedAt,_tmpSecurityStatus,_tmpWasAlertSent);
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
  public Object getUniqueTowerCount(final long startTime,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(DISTINCT cellId) FROM cell_tower_history WHERE connectedAt >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
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
  public Object getUnresolvedIncidents(
      final Continuation<? super List<CellTowerIncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM cell_tower_incidents WHERE wasResolved = 0 ORDER BY occurredAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerIncidentEntity>>() {
      @Override
      @NonNull
      public List<CellTowerIncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfIncidentType = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentType");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIndicators = CursorUtil.getColumnIndexOrThrow(_cursor, "indicators");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfWasEmailSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasEmailSent");
          final int _cursorIndexOfWasResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "wasResolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<CellTowerIncidentEntity> _result = new ArrayList<CellTowerIncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerIncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            if (_cursor.isNull(_cursorIndexOfLac)) {
              _tmpLac = null;
            } else {
              _tmpLac = _cursor.getString(_cursorIndexOfLac);
            }
            final String _tmpIncidentType;
            _tmpIncidentType = _cursor.getString(_cursorIndexOfIncidentType);
            final String _tmpRiskLevel;
            _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIndicators;
            _tmpIndicators = _cursor.getString(_cursorIndexOfIndicators);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            final boolean _tmpWasEmailSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasEmailSent);
            _tmpWasEmailSent = _tmp != 0;
            final boolean _tmpWasResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasResolved);
            _tmpWasResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new CellTowerIncidentEntity(_tmpId,_tmpCellId,_tmpLac,_tmpIncidentType,_tmpRiskLevel,_tmpDescription,_tmpIndicators,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpOccurredAt,_tmpWasEmailSent,_tmpWasResolved,_tmpResolvedAt);
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
  public Object getRecentIncidents(final int limit,
      final Continuation<? super List<CellTowerIncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM cell_tower_incidents ORDER BY occurredAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerIncidentEntity>>() {
      @Override
      @NonNull
      public List<CellTowerIncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfIncidentType = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentType");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIndicators = CursorUtil.getColumnIndexOrThrow(_cursor, "indicators");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfWasEmailSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasEmailSent");
          final int _cursorIndexOfWasResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "wasResolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<CellTowerIncidentEntity> _result = new ArrayList<CellTowerIncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerIncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            if (_cursor.isNull(_cursorIndexOfLac)) {
              _tmpLac = null;
            } else {
              _tmpLac = _cursor.getString(_cursorIndexOfLac);
            }
            final String _tmpIncidentType;
            _tmpIncidentType = _cursor.getString(_cursorIndexOfIncidentType);
            final String _tmpRiskLevel;
            _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIndicators;
            _tmpIndicators = _cursor.getString(_cursorIndexOfIndicators);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            final boolean _tmpWasEmailSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasEmailSent);
            _tmpWasEmailSent = _tmp != 0;
            final boolean _tmpWasResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasResolved);
            _tmpWasResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new CellTowerIncidentEntity(_tmpId,_tmpCellId,_tmpLac,_tmpIncidentType,_tmpRiskLevel,_tmpDescription,_tmpIndicators,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpOccurredAt,_tmpWasEmailSent,_tmpWasResolved,_tmpResolvedAt);
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
  public Object getPendingEmailIncidents(
      final Continuation<? super List<CellTowerIncidentEntity>> $completion) {
    final String _sql = "SELECT * FROM cell_tower_incidents WHERE wasEmailSent = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerIncidentEntity>>() {
      @Override
      @NonNull
      public List<CellTowerIncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfIncidentType = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentType");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIndicators = CursorUtil.getColumnIndexOrThrow(_cursor, "indicators");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfWasEmailSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasEmailSent");
          final int _cursorIndexOfWasResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "wasResolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<CellTowerIncidentEntity> _result = new ArrayList<CellTowerIncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerIncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            if (_cursor.isNull(_cursorIndexOfLac)) {
              _tmpLac = null;
            } else {
              _tmpLac = _cursor.getString(_cursorIndexOfLac);
            }
            final String _tmpIncidentType;
            _tmpIncidentType = _cursor.getString(_cursorIndexOfIncidentType);
            final String _tmpRiskLevel;
            _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIndicators;
            _tmpIndicators = _cursor.getString(_cursorIndexOfIndicators);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            final boolean _tmpWasEmailSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasEmailSent);
            _tmpWasEmailSent = _tmp != 0;
            final boolean _tmpWasResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasResolved);
            _tmpWasResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new CellTowerIncidentEntity(_tmpId,_tmpCellId,_tmpLac,_tmpIncidentType,_tmpRiskLevel,_tmpDescription,_tmpIndicators,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpOccurredAt,_tmpWasEmailSent,_tmpWasResolved,_tmpResolvedAt);
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
  public Object getIncidentsForTower(final String cellId, final long sinceTime,
      final Continuation<? super List<CellTowerIncidentEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM cell_tower_incidents \n"
            + "        WHERE cellId = ? AND occurredAt >= ? \n"
            + "        ORDER BY occurredAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cellId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, sinceTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerIncidentEntity>>() {
      @Override
      @NonNull
      public List<CellTowerIncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCellId = CursorUtil.getColumnIndexOrThrow(_cursor, "cellId");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfIncidentType = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentType");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIndicators = CursorUtil.getColumnIndexOrThrow(_cursor, "indicators");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfWasEmailSent = CursorUtil.getColumnIndexOrThrow(_cursor, "wasEmailSent");
          final int _cursorIndexOfWasResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "wasResolved");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final List<CellTowerIncidentEntity> _result = new ArrayList<CellTowerIncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerIncidentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCellId;
            _tmpCellId = _cursor.getString(_cursorIndexOfCellId);
            final String _tmpLac;
            if (_cursor.isNull(_cursorIndexOfLac)) {
              _tmpLac = null;
            } else {
              _tmpLac = _cursor.getString(_cursorIndexOfLac);
            }
            final String _tmpIncidentType;
            _tmpIncidentType = _cursor.getString(_cursorIndexOfIncidentType);
            final String _tmpRiskLevel;
            _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIndicators;
            _tmpIndicators = _cursor.getString(_cursorIndexOfIndicators);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpAreaName;
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _tmpAreaName = null;
            } else {
              _tmpAreaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            final boolean _tmpWasEmailSent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasEmailSent);
            _tmpWasEmailSent = _tmp != 0;
            final boolean _tmpWasResolved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasResolved);
            _tmpWasResolved = _tmp_1 != 0;
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            _item = new CellTowerIncidentEntity(_tmpId,_tmpCellId,_tmpLac,_tmpIncidentType,_tmpRiskLevel,_tmpDescription,_tmpIndicators,_tmpLatitude,_tmpLongitude,_tmpAreaName,_tmpOccurredAt,_tmpWasEmailSent,_tmpWasResolved,_tmpResolvedAt);
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
  public Object getHighRiskIncidentCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM cell_tower_incidents WHERE riskLevel = 'HIGH' OR riskLevel = 'CRITICAL'";
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
