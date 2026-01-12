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
import com.sentinelguard.data.database.entities.UserEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLoginAttempts;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSuccessfulLogin;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBiometricEnabled;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePassword;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `users` (`id`,`email`,`passwordHash`,`biometricEnabled`,`createdAt`,`lastLoginAt`,`failedLoginAttempts`,`lockoutUntil`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEmail());
        statement.bindString(3, entity.getPasswordHash());
        final int _tmp = entity.getBiometricEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getLastLoginAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastLoginAt());
        }
        statement.bindLong(7, entity.getFailedLoginAttempts());
        if (entity.getLockoutUntil() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLockoutUntil());
        }
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`email` = ?,`passwordHash` = ?,`biometricEnabled` = ?,`createdAt` = ?,`lastLoginAt` = ?,`failedLoginAttempts` = ?,`lockoutUntil` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEmail());
        statement.bindString(3, entity.getPasswordHash());
        final int _tmp = entity.getBiometricEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getLastLoginAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastLoginAt());
        }
        statement.bindLong(7, entity.getFailedLoginAttempts());
        if (entity.getLockoutUntil() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLockoutUntil());
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateLoginAttempts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET failedLoginAttempts = ?, lockoutUntil = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSuccessfulLogin = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET lastLoginAt = ?, failedLoginAttempts = 0, lockoutUntil = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBiometricEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET biometricEnabled = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePassword = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET passwordHash = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final UserEntity user, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUserEntity.insertAndReturnId(user);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserEntity.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLoginAttempts(final long userId, final int attempts, final Long lockoutUntil,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLoginAttempts.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, attempts);
        _argIndex = 2;
        if (lockoutUntil == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, lockoutUntil);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfUpdateLoginAttempts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSuccessfulLogin(final long userId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSuccessfulLogin.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfUpdateSuccessfulLogin.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBiometricEnabled(final long userId, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBiometricEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfUpdateBiometricEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePassword(final long userId, final String newHash,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePassword.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, newHash);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfUpdatePassword.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfDeleteById.release(_stmt);
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
  public Object getById(final long id, final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final int _cursorIndexOfFailedLoginAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedLoginAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            final int _tmpFailedLoginAttempts;
            _tmpFailedLoginAttempts = _cursor.getInt(_cursorIndexOfFailedLoginAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new UserEntity(_tmpId,_tmpEmail,_tmpPasswordHash,_tmpBiometricEnabled,_tmpCreatedAt,_tmpLastLoginAt,_tmpFailedLoginAttempts,_tmpLockoutUntil);
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
  public Object getByEmail(final String email, final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE email = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, email);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final int _cursorIndexOfFailedLoginAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedLoginAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            final int _tmpFailedLoginAttempts;
            _tmpFailedLoginAttempts = _cursor.getInt(_cursorIndexOfFailedLoginAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new UserEntity(_tmpId,_tmpEmail,_tmpPasswordHash,_tmpBiometricEnabled,_tmpCreatedAt,_tmpLastLoginAt,_tmpFailedLoginAttempts,_tmpLockoutUntil);
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
  public Object getFirstUser(final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final int _cursorIndexOfFailedLoginAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedLoginAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            final int _tmpFailedLoginAttempts;
            _tmpFailedLoginAttempts = _cursor.getInt(_cursorIndexOfFailedLoginAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new UserEntity(_tmpId,_tmpEmail,_tmpPasswordHash,_tmpBiometricEnabled,_tmpCreatedAt,_tmpLastLoginAt,_tmpFailedLoginAttempts,_tmpLockoutUntil);
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
  public Flow<UserEntity> observeFirstUser() {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final int _cursorIndexOfFailedLoginAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedLoginAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            final int _tmpFailedLoginAttempts;
            _tmpFailedLoginAttempts = _cursor.getInt(_cursorIndexOfFailedLoginAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new UserEntity(_tmpId,_tmpEmail,_tmpPasswordHash,_tmpBiometricEnabled,_tmpCreatedAt,_tmpLastLoginAt,_tmpFailedLoginAttempts,_tmpLockoutUntil);
          } else {
            _result = null;
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
  public Object getUserCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM users";
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
