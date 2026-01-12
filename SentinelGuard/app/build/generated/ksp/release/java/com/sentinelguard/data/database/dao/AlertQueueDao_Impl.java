package com.sentinelguard.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sentinelguard.data.database.entities.AlertQueueEntity;
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
public final class AlertQueueDao_Impl implements AlertQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AlertQueueEntity> __insertionAdapterOfAlertQueueEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfIncrementRetry;

  private final SharedSQLiteStatement __preparedStmtOfMarkSent;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AlertQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlertQueueEntity = new EntityInsertionAdapter<AlertQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `alert_queue` (`id`,`recipientEmail`,`subject`,`body`,`status`,`incidentId`,`retryCount`,`lastError`,`nextRetryAt`,`createdAt`,`sentAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlertQueueEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getRecipientEmail());
        statement.bindString(3, entity.getSubject());
        statement.bindString(4, entity.getBody());
        statement.bindString(5, entity.getStatus());
        if (entity.getIncidentId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIncidentId());
        }
        statement.bindLong(7, entity.getRetryCount());
        if (entity.getLastError() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastError());
        }
        if (entity.getNextRetryAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextRetryAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
        if (entity.getSentAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getSentAt());
        }
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_queue SET status = ?, lastError = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementRetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_queue SET retryCount = retryCount + 1, nextRetryAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkSent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_queue SET status = 'SENT', sentAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alert_queue WHERE createdAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alert_queue";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AlertQueueEntity alert, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlertQueueEntity.insertAndReturnId(alert);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long id, final String status, final String error,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
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
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementRetry(final long id, final long nextRetryAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementRetry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nextRetryAt);
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
          __preparedStmtOfIncrementRetry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markSent(final long id, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfMarkSent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
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
  public Object getPending(final Continuation<? super List<AlertQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_queue WHERE status = 'PENDING' ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertQueueEntity>>() {
      @Override
      @NonNull
      public List<AlertQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final List<AlertQueueEntity> _result = new ArrayList<AlertQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertQueueEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
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
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            _item = new AlertQueueEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt,_tmpCreatedAt,_tmpSentAt);
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
  public Object getByStatus(final String status,
      final Continuation<? super List<AlertQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_queue WHERE status = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertQueueEntity>>() {
      @Override
      @NonNull
      public List<AlertQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final List<AlertQueueEntity> _result = new ArrayList<AlertQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertQueueEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
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
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            _item = new AlertQueueEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt,_tmpCreatedAt,_tmpSentAt);
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
  public Object getByIncidentId(final long incidentId,
      final Continuation<? super List<AlertQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM alert_queue WHERE incidentId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, incidentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertQueueEntity>>() {
      @Override
      @NonNull
      public List<AlertQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipientEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientEmail");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfNextRetryAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextRetryAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSentAt = CursorUtil.getColumnIndexOrThrow(_cursor, "sentAt");
          final List<AlertQueueEntity> _result = new ArrayList<AlertQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertQueueEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRecipientEmail;
            _tmpRecipientEmail = _cursor.getString(_cursorIndexOfRecipientEmail);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpIncidentId;
            if (_cursor.isNull(_cursorIndexOfIncidentId)) {
              _tmpIncidentId = null;
            } else {
              _tmpIncidentId = _cursor.getLong(_cursorIndexOfIncidentId);
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
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpSentAt;
            if (_cursor.isNull(_cursorIndexOfSentAt)) {
              _tmpSentAt = null;
            } else {
              _tmpSentAt = _cursor.getLong(_cursorIndexOfSentAt);
            }
            _item = new AlertQueueEntity(_tmpId,_tmpRecipientEmail,_tmpSubject,_tmpBody,_tmpStatus,_tmpIncidentId,_tmpRetryCount,_tmpLastError,_tmpNextRetryAt,_tmpCreatedAt,_tmpSentAt);
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
