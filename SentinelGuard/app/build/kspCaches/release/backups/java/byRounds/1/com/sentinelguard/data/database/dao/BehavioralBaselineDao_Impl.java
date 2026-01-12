package com.sentinelguard.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sentinelguard.data.database.Converters;
import com.sentinelguard.data.database.entities.BaselineMetricType;
import com.sentinelguard.data.database.entities.BehavioralBaselineEntity;
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
public final class BehavioralBaselineDao_Impl implements BehavioralBaselineDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BehavioralBaselineEntity> __insertionAdapterOfBehavioralBaselineEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<BehavioralBaselineEntity> __updateAdapterOfBehavioralBaselineEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkLearningComplete;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByType;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final EntityUpsertionAdapter<BehavioralBaselineEntity> __upsertionAdapterOfBehavioralBaselineEntity;

  public BehavioralBaselineDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBehavioralBaselineEntity = new EntityInsertionAdapter<BehavioralBaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `behavioral_baselines` (`id`,`metricType`,`baselineValue`,`variance`,`confidence`,`sampleCount`,`createdAt`,`updatedAt`,`learningComplete`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BehavioralBaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromBaselineMetricType(entity.getMetricType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getBaselineValue());
        if (entity.getVariance() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getVariance());
        }
        statement.bindDouble(5, entity.getConfidence());
        statement.bindLong(6, entity.getSampleCount());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        final int _tmp_1 = entity.getLearningComplete() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
      }
    };
    this.__updateAdapterOfBehavioralBaselineEntity = new EntityDeletionOrUpdateAdapter<BehavioralBaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `behavioral_baselines` SET `id` = ?,`metricType` = ?,`baselineValue` = ?,`variance` = ?,`confidence` = ?,`sampleCount` = ?,`createdAt` = ?,`updatedAt` = ?,`learningComplete` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BehavioralBaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromBaselineMetricType(entity.getMetricType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getBaselineValue());
        if (entity.getVariance() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getVariance());
        }
        statement.bindDouble(5, entity.getConfidence());
        statement.bindLong(6, entity.getSampleCount());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        final int _tmp_1 = entity.getLearningComplete() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfMarkLearningComplete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE behavioral_baselines SET learningComplete = 1, updatedAt = ? WHERE metricType = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM behavioral_baselines WHERE metricType = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM behavioral_baselines";
        return _query;
      }
    };
    this.__upsertionAdapterOfBehavioralBaselineEntity = new EntityUpsertionAdapter<BehavioralBaselineEntity>(new EntityInsertionAdapter<BehavioralBaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `behavioral_baselines` (`id`,`metricType`,`baselineValue`,`variance`,`confidence`,`sampleCount`,`createdAt`,`updatedAt`,`learningComplete`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BehavioralBaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromBaselineMetricType(entity.getMetricType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getBaselineValue());
        if (entity.getVariance() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getVariance());
        }
        statement.bindDouble(5, entity.getConfidence());
        statement.bindLong(6, entity.getSampleCount());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        final int _tmp_1 = entity.getLearningComplete() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
      }
    }, new EntityDeletionOrUpdateAdapter<BehavioralBaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `behavioral_baselines` SET `id` = ?,`metricType` = ?,`baselineValue` = ?,`variance` = ?,`confidence` = ?,`sampleCount` = ?,`createdAt` = ?,`updatedAt` = ?,`learningComplete` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BehavioralBaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromBaselineMetricType(entity.getMetricType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getBaselineValue());
        if (entity.getVariance() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getVariance());
        }
        statement.bindDouble(5, entity.getConfidence());
        statement.bindLong(6, entity.getSampleCount());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        final int _tmp_1 = entity.getLearningComplete() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getId());
      }
    });
  }

  @Override
  public Object insert(final BehavioralBaselineEntity baseline,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBehavioralBaselineEntity.insertAndReturnId(baseline);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BehavioralBaselineEntity baseline,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBehavioralBaselineEntity.handle(baseline);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markLearningComplete(final BaselineMetricType type, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkLearningComplete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        final String _tmp = __converters.fromBaselineMetricType(type);
        _stmt.bindString(_argIndex, _tmp);
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
          __preparedStmtOfMarkLearningComplete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByType(final BaselineMetricType type,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByType.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromBaselineMetricType(type);
        _stmt.bindString(_argIndex, _tmp);
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
          __preparedStmtOfDeleteByType.release(_stmt);
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
  public Object upsert(final BehavioralBaselineEntity baseline,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBehavioralBaselineEntity.upsert(baseline);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id,
      final Continuation<? super BehavioralBaselineEntity> $completion) {
    final String _sql = "SELECT * FROM behavioral_baselines WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BehavioralBaselineEntity>() {
      @Override
      @Nullable
      public BehavioralBaselineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final BehavioralBaselineEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_1 != 0;
            _result = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Object getByType(final BaselineMetricType type,
      final Continuation<? super BehavioralBaselineEntity> $completion) {
    final String _sql = "SELECT * FROM behavioral_baselines WHERE metricType = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromBaselineMetricType(type);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BehavioralBaselineEntity>() {
      @Override
      @Nullable
      public BehavioralBaselineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final BehavioralBaselineEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp_1);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_2 != 0;
            _result = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Object getAll(final Continuation<? super List<BehavioralBaselineEntity>> $completion) {
    final String _sql = "SELECT * FROM behavioral_baselines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BehavioralBaselineEntity>>() {
      @Override
      @NonNull
      public List<BehavioralBaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final List<BehavioralBaselineEntity> _result = new ArrayList<BehavioralBaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralBaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_1 != 0;
            _item = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Flow<List<BehavioralBaselineEntity>> observeAll() {
    final String _sql = "SELECT * FROM behavioral_baselines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"behavioral_baselines"}, new Callable<List<BehavioralBaselineEntity>>() {
      @Override
      @NonNull
      public List<BehavioralBaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final List<BehavioralBaselineEntity> _result = new ArrayList<BehavioralBaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralBaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_1 != 0;
            _item = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Object getCompletedBaselines(
      final Continuation<? super List<BehavioralBaselineEntity>> $completion) {
    final String _sql = "SELECT * FROM behavioral_baselines WHERE learningComplete = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BehavioralBaselineEntity>>() {
      @Override
      @NonNull
      public List<BehavioralBaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final List<BehavioralBaselineEntity> _result = new ArrayList<BehavioralBaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralBaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_1 != 0;
            _item = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Object getIncompleteBaselines(
      final Continuation<? super List<BehavioralBaselineEntity>> $completion) {
    final String _sql = "SELECT * FROM behavioral_baselines WHERE learningComplete = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BehavioralBaselineEntity>>() {
      @Override
      @NonNull
      public List<BehavioralBaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfBaselineValue = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineValue");
          final int _cursorIndexOfVariance = CursorUtil.getColumnIndexOrThrow(_cursor, "variance");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfLearningComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "learningComplete");
          final List<BehavioralBaselineEntity> _result = new ArrayList<BehavioralBaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BehavioralBaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final BaselineMetricType _tmpMetricType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetricType);
            _tmpMetricType = __converters.toBaselineMetricType(_tmp);
            final String _tmpBaselineValue;
            _tmpBaselineValue = _cursor.getString(_cursorIndexOfBaselineValue);
            final Double _tmpVariance;
            if (_cursor.isNull(_cursorIndexOfVariance)) {
              _tmpVariance = null;
            } else {
              _tmpVariance = _cursor.getDouble(_cursorIndexOfVariance);
            }
            final double _tmpConfidence;
            _tmpConfidence = _cursor.getDouble(_cursorIndexOfConfidence);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final boolean _tmpLearningComplete;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLearningComplete);
            _tmpLearningComplete = _tmp_1 != 0;
            _item = new BehavioralBaselineEntity(_tmpId,_tmpMetricType,_tmpBaselineValue,_tmpVariance,_tmpConfidence,_tmpSampleCount,_tmpCreatedAt,_tmpUpdatedAt,_tmpLearningComplete);
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
  public Object getAverageConfidence(final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(confidence) FROM behavioral_baselines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
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
  public Object getCompletedCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM behavioral_baselines WHERE learningComplete = 1";
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
