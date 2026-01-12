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
import com.sentinelguard.data.database.Converters;
import com.sentinelguard.data.database.entities.RiskLevel;
import com.sentinelguard.data.database.entities.RiskScoreEntity;
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
public final class RiskScoreDao_Impl implements RiskScoreDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RiskScoreEntity> __insertionAdapterOfRiskScoreEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<RiskScoreEntity> __updateAdapterOfRiskScoreEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDecayedScore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RiskScoreDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRiskScoreEntity = new EntityInsertionAdapter<RiskScoreEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `risk_scores` (`id`,`totalScore`,`riskLevel`,`signalContributions`,`triggeredAction`,`triggerReason`,`timestamp`,`decayed`,`currentScore`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiskScoreEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTotalScore());
        final String _tmp = __converters.fromRiskLevel(entity.getRiskLevel());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getSignalContributions());
        final int _tmp_1 = entity.getTriggeredAction() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getTriggerReason() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTriggerReason());
        }
        statement.bindLong(7, entity.getTimestamp());
        final int _tmp_2 = entity.getDecayed() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        if (entity.getCurrentScore() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCurrentScore());
        }
      }
    };
    this.__updateAdapterOfRiskScoreEntity = new EntityDeletionOrUpdateAdapter<RiskScoreEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `risk_scores` SET `id` = ?,`totalScore` = ?,`riskLevel` = ?,`signalContributions` = ?,`triggeredAction` = ?,`triggerReason` = ?,`timestamp` = ?,`decayed` = ?,`currentScore` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiskScoreEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTotalScore());
        final String _tmp = __converters.fromRiskLevel(entity.getRiskLevel());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getSignalContributions());
        final int _tmp_1 = entity.getTriggeredAction() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getTriggerReason() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTriggerReason());
        }
        statement.bindLong(7, entity.getTimestamp());
        final int _tmp_2 = entity.getDecayed() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        if (entity.getCurrentScore() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCurrentScore());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateDecayedScore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE risk_scores SET currentScore = ?, decayed = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM risk_scores WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM risk_scores";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RiskScoreEntity riskScore,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRiskScoreEntity.insertAndReturnId(riskScore);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RiskScoreEntity riskScore,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRiskScoreEntity.handle(riskScore);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDecayedScore(final long id, final int decayedScore,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDecayedScore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, decayedScore);
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
          __preparedStmtOfUpdateDecayedScore.release(_stmt);
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
  public Object getById(final long id, final Continuation<? super RiskScoreEntity> $completion) {
    final String _sql = "SELECT * FROM risk_scores WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RiskScoreEntity>() {
      @Override
      @Nullable
      public RiskScoreEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final RiskScoreEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _result = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getLatest(final Continuation<? super RiskScoreEntity> $completion) {
    final String _sql = "SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RiskScoreEntity>() {
      @Override
      @Nullable
      public RiskScoreEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final RiskScoreEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _result = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Flow<RiskScoreEntity> observeLatest() {
    final String _sql = "SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"risk_scores"}, new Callable<RiskScoreEntity>() {
      @Override
      @Nullable
      public RiskScoreEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final RiskScoreEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _result = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getRecent(final int limit,
      final Continuation<? super List<RiskScoreEntity>> $completion) {
    final String _sql = "SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RiskScoreEntity>>() {
      @Override
      @NonNull
      public List<RiskScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final List<RiskScoreEntity> _result = new ArrayList<RiskScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _item = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Flow<List<RiskScoreEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"risk_scores"}, new Callable<List<RiskScoreEntity>>() {
      @Override
      @NonNull
      public List<RiskScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final List<RiskScoreEntity> _result = new ArrayList<RiskScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _item = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getByLevel(final RiskLevel level,
      final Continuation<? super List<RiskScoreEntity>> $completion) {
    final String _sql = "SELECT * FROM risk_scores WHERE riskLevel = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromRiskLevel(level);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RiskScoreEntity>>() {
      @Override
      @NonNull
      public List<RiskScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final List<RiskScoreEntity> _result = new ArrayList<RiskScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp_1);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_2 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_3 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _item = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getAboveScore(final int minScore,
      final Continuation<? super List<RiskScoreEntity>> $completion) {
    final String _sql = "SELECT * FROM risk_scores WHERE totalScore >= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, minScore);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RiskScoreEntity>>() {
      @Override
      @NonNull
      public List<RiskScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final List<RiskScoreEntity> _result = new ArrayList<RiskScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _item = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getTriggeredActions(final Continuation<? super List<RiskScoreEntity>> $completion) {
    final String _sql = "SELECT * FROM risk_scores WHERE triggeredAction = 1 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RiskScoreEntity>>() {
      @Override
      @NonNull
      public List<RiskScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSignalContributions = CursorUtil.getColumnIndexOrThrow(_cursor, "signalContributions");
          final int _cursorIndexOfTriggeredAction = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredAction");
          final int _cursorIndexOfTriggerReason = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerReason");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDecayed = CursorUtil.getColumnIndexOrThrow(_cursor, "decayed");
          final int _cursorIndexOfCurrentScore = CursorUtil.getColumnIndexOrThrow(_cursor, "currentScore");
          final List<RiskScoreEntity> _result = new ArrayList<RiskScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final RiskLevel _tmpRiskLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRiskLevel);
            _tmpRiskLevel = __converters.toRiskLevel(_tmp);
            final String _tmpSignalContributions;
            _tmpSignalContributions = _cursor.getString(_cursorIndexOfSignalContributions);
            final boolean _tmpTriggeredAction;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfTriggeredAction);
            _tmpTriggeredAction = _tmp_1 != 0;
            final String _tmpTriggerReason;
            if (_cursor.isNull(_cursorIndexOfTriggerReason)) {
              _tmpTriggerReason = null;
            } else {
              _tmpTriggerReason = _cursor.getString(_cursorIndexOfTriggerReason);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpDecayed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDecayed);
            _tmpDecayed = _tmp_2 != 0;
            final Integer _tmpCurrentScore;
            if (_cursor.isNull(_cursorIndexOfCurrentScore)) {
              _tmpCurrentScore = null;
            } else {
              _tmpCurrentScore = _cursor.getInt(_cursorIndexOfCurrentScore);
            }
            _item = new RiskScoreEntity(_tmpId,_tmpTotalScore,_tmpRiskLevel,_tmpSignalContributions,_tmpTriggeredAction,_tmpTriggerReason,_tmpTimestamp,_tmpDecayed,_tmpCurrentScore);
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
  public Object getAverageScoreSince(final long since,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(totalScore) FROM risk_scores WHERE timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
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
  public Object getMaxScoreSince(final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(totalScore) FROM risk_scores WHERE timestamp >= ?";
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
