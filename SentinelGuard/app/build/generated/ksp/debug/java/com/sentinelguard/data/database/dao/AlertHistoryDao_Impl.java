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
import com.sentinelguard.data.database.entities.AlertHistoryEntity;
import com.sentinelguard.data.database.entities.AlertStatus;
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
public final class AlertHistoryDao_Impl implements AlertHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AlertHistoryEntity> __insertionAdapterOfAlertHistoryEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<AlertHistoryEntity> __updateAdapterOfAlertHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatusSent;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatusFailed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AlertHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlertHistoryEntity = new EntityInsertionAdapter<AlertHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `alert_history` (`id`,`recipientEmail`,`subject`,`body`,`status`,`incidentId`,`createdAt`,`sentAt`,`retryCount`,`lastError`,`nextRetryAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlertHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getRecipientEmail());
        statement.bindString(3, entity.getSubject());
        statement.bindString(4, entity.getBody());
        final String _tmp = __converters.fromAlertStatus(entity.getStatus());
        statement.bindString(5, _tmp);
        if (entity.getIncidentId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIncidentId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getSentAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getSentAt());
        }
        statement.bindLong(9, entity.getRetryCount());
        if (entity.getLastError() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLastError());
        }
        if (entity.getNextRetryAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getNextRetryAt());
        }
      }
    };
    this.__updateAdapterOfAlertHistoryEntity = new EntityDeletionOrUpdateAdapter<AlertHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `alert_history` SET `id` = ?,`recipientEmail` = ?,`subject` = ?,`body` = ?,`status` = ?,`incidentId` = ?,`createdAt` = ?,`sentAt` = ?,`retryCount` = ?,`lastError` = ?,`nextRetryAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlertHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getRecipientEmail());
        statement.bindString(3, entity.getSubject());
        statement.bindString(4, entity.getBody());
        final String _tmp = __converters.fromAlertStatus(entity.getStatus());
        statement.bindString(5, _tmp);
        if (entity.getIncidentId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIncidentId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        if (entity.getSentAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getSentAt());
        }
        statement.bindLong(9, entity.getRetryCount());
        if (entity.getLastError() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLastError());
        }
        if (entity.getNextRetryAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getNextRetryAt());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatusSent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_history SET status = ?, sentAt = ?, retryCount = retryCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStatusFailed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_history SET status = ?, lastError = ?, retryCount = retryCount + 1, nextRetryAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alert_history WHERE createdAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alert_history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AlertHistoryEntity alert,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlertHistoryEntity.insertAndReturnId(alert);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AlertHistoryEntity alert,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAlertHistoryEntity.handle(alert);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatusSent(final long id, final AlertStatus status, final long sentAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatusSent.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromAlertStatus(status);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, sentAt);
        _argIndex = 3;
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
          __preparedStmtOfUpdateStatusSent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatusFailed(final long id, final AlertStatus status, final String error,
      final Long nextRetry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatusFailed.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromAlertStatus(status);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
        _argIndex = 3;
        if (nextRetry == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, nextRetry);
        }
        _argIndex = 4;
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
          __preparedStmtOfUpdateStatusFailed.release(_stmt);
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
  public Object getById(final long id, final Continuation<? super AlertHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM alert_history WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AlertHistoryEntity>() {
      @Override
      @Nullable
      public AlertHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final AlertHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _result = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
      final Continuation<? super List<AlertHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_history ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Flow<List<AlertHistoryEntity>> observeAll() {
    final String _sql = "SELECT * FROM alert_history ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alert_history"}, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Flow<List<AlertHistoryEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM alert_history ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alert_history"}, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Object getByStatus(final AlertStatus status,
      final Continuation<? super List<AlertHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_history WHERE status = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromAlertStatus(status);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp_1);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Flow<List<AlertHistoryEntity>> observeByStatus(final AlertStatus status) {
    final String _sql = "SELECT * FROM alert_history WHERE status = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromAlertStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alert_history"}, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp_1);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Object getByStatuses(final List<? extends AlertStatus> statuses,
      final Continuation<? super List<AlertHistoryEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM alert_history WHERE status IN (");
    final int _inputSize = statuses.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ORDER BY createdAt ASC");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (AlertStatus _item : statuses) {
      final String _tmp = __converters.fromAlertStatus(_item);
      _statement.bindString(_argIndex, _tmp);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp_1);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item_1 = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Object getPendingSend(final long now,
      final Continuation<? super List<AlertHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_history WHERE status = 'QUEUED' AND (nextRetryAt IS NULL OR nextRetryAt <= ?) ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
  public Object countByStatus(final AlertStatus status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM alert_history WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromAlertStatus(status);
    _statement.bindString(_argIndex, _tmp);
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
  public Object getByIncidentId(final long incidentId,
      final Continuation<? super List<AlertHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_history WHERE incidentId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, incidentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertHistoryEntity>>() {
      @Override
      @NonNull
      public List<AlertHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final List<AlertHistoryEntity> _result = new ArrayList<AlertHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final AlertStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toAlertStatus(_tmp);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final Long _tmpNextRetryAt;
            if (_cursor.isNull(_cursorIndexOfNextRetryAt)) {
              _tmpNextRetryAt = null;
            } else {
              _tmpNextRetryAt = _cursor.getLong(_cursorIndexOfNextRetryAt);
            }
            _item = new AlertHistoryEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpCreatedAt,_tmpSentAt,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt);
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
