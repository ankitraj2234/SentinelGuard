package com.sentinelguard.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.sentinelguard.data.database.dao.AlertHistoryDao;
import com.sentinelguard.data.database.dao.AlertHistoryDao_Impl;
import com.sentinelguard.data.database.dao.AlertQueueDao;
import com.sentinelguard.data.database.dao.AlertQueueDao_Impl;
import com.sentinelguard.data.database.dao.AppUsagePatternDao;
import com.sentinelguard.data.database.dao.AppUsagePatternDao_Impl;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao_Impl;
import com.sentinelguard.data.database.dao.BehavioralBaselineDao;
import com.sentinelguard.data.database.dao.BehavioralBaselineDao_Impl;
import com.sentinelguard.data.database.dao.IncidentDao;
import com.sentinelguard.data.database.dao.IncidentDao_Impl;
import com.sentinelguard.data.database.dao.KnownNetworkDao;
import com.sentinelguard.data.database.dao.KnownNetworkDao_Impl;
import com.sentinelguard.data.database.dao.LocationClusterDao;
import com.sentinelguard.data.database.dao.LocationClusterDao_Impl;
import com.sentinelguard.data.database.dao.RiskScoreDao;
import com.sentinelguard.data.database.dao.RiskScoreDao_Impl;
import com.sentinelguard.data.database.dao.SecuritySignalDao;
import com.sentinelguard.data.database.dao.SecuritySignalDao_Impl;
import com.sentinelguard.data.database.dao.UnlockPatternDao;
import com.sentinelguard.data.database.dao.UnlockPatternDao_Impl;
import com.sentinelguard.data.database.dao.UserDao;
import com.sentinelguard.data.database.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile SecuritySignalDao _securitySignalDao;

  private volatile BehavioralBaselineDao _behavioralBaselineDao;

  private volatile RiskScoreDao _riskScoreDao;

  private volatile IncidentDao _incidentDao;

  private volatile AlertHistoryDao _alertHistoryDao;

  private volatile AlertQueueDao _alertQueueDao;

  private volatile AppUsagePatternDao _appUsagePatternDao;

  private volatile LocationClusterDao _locationClusterDao;

  private volatile KnownNetworkDao _knownNetworkDao;

  private volatile UnlockPatternDao _unlockPatternDao;

  private volatile BehavioralAnomalyDao _behavioralAnomalyDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `email` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `biometricEnabled` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastLoginAt` INTEGER, `failedLoginAttempts` INTEGER NOT NULL, `lockoutUntil` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `security_signals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `signalType` TEXT NOT NULL, `value` TEXT, `timestamp` INTEGER NOT NULL, `metadata` TEXT, `processed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `behavioral_baselines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `metricType` TEXT NOT NULL, `baselineValue` TEXT NOT NULL, `variance` REAL, `confidence` REAL NOT NULL, `sampleCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `learningComplete` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `risk_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `totalScore` INTEGER NOT NULL, `riskLevel` TEXT NOT NULL, `signalContributions` TEXT NOT NULL, `triggeredAction` INTEGER NOT NULL, `triggerReason` TEXT, `timestamp` INTEGER NOT NULL, `decayed` INTEGER NOT NULL, `currentScore` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `incidents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `severity` TEXT NOT NULL, `riskScore` INTEGER NOT NULL, `triggeredBy` TEXT NOT NULL, `actionsTaken` TEXT NOT NULL, `summary` TEXT NOT NULL, `location` TEXT, `deviceState` TEXT, `timestamp` INTEGER NOT NULL, `resolved` INTEGER NOT NULL, `resolvedAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alert_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipientEmail` TEXT NOT NULL, `subject` TEXT NOT NULL, `body` TEXT NOT NULL, `status` TEXT NOT NULL, `incidentId` INTEGER, `createdAt` INTEGER NOT NULL, `sentAt` INTEGER, `retryCount` INTEGER NOT NULL, `lastError` TEXT, `nextRetryAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alert_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipientEmail` TEXT NOT NULL, `subject` TEXT NOT NULL, `body` TEXT NOT NULL, `status` TEXT NOT NULL, `incidentId` INTEGER, `retryCount` INTEGER NOT NULL, `lastError` TEXT, `nextRetryAt` INTEGER, `createdAt` INTEGER NOT NULL, `sentAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_usage_patterns` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `hourOfDay` INTEGER NOT NULL, `dayOfWeek` INTEGER NOT NULL, `usageCount` INTEGER NOT NULL, `avgDurationMs` INTEGER NOT NULL, `lastUsed` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `location_clusters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `centerLatitude` REAL NOT NULL, `centerLongitude` REAL NOT NULL, `radiusMeters` REAL NOT NULL, `label` TEXT, `visitCount` INTEGER NOT NULL, `totalTimeSpentMs` INTEGER NOT NULL, `lastVisited` INTEGER NOT NULL, `isTrusted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `known_networks` (`ssid` TEXT NOT NULL, `bssid` TEXT, `isSecure` INTEGER NOT NULL, `isTrusted` INTEGER NOT NULL, `connectionCount` INTEGER NOT NULL, `lastConnected` INTEGER NOT NULL, `totalTimeConnectedMs` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`ssid`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `unlock_patterns` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hourOfDay` INTEGER NOT NULL, `dayOfWeek` INTEGER NOT NULL, `unlockCount` INTEGER NOT NULL, `failedAttempts` INTEGER NOT NULL, `avgSessionLengthMs` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `behavioral_anomalies` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `anomalyType` TEXT NOT NULL, `description` TEXT NOT NULL, `severity` INTEGER NOT NULL, `riskPoints` INTEGER NOT NULL, `resolved` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '30a5d771e733972ac02c679caade4951')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `security_signals`");
        db.execSQL("DROP TABLE IF EXISTS `behavioral_baselines`");
        db.execSQL("DROP TABLE IF EXISTS `risk_scores`");
        db.execSQL("DROP TABLE IF EXISTS `incidents`");
        db.execSQL("DROP TABLE IF EXISTS `alert_history`");
        db.execSQL("DROP TABLE IF EXISTS `alert_queue`");
        db.execSQL("DROP TABLE IF EXISTS `app_usage_patterns`");
        db.execSQL("DROP TABLE IF EXISTS `location_clusters`");
        db.execSQL("DROP TABLE IF EXISTS `known_networks`");
        db.execSQL("DROP TABLE IF EXISTS `unlock_patterns`");
        db.execSQL("DROP TABLE IF EXISTS `behavioral_anomalies`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(8);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("passwordHash", new TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("biometricEnabled", new TableInfo.Column("biometricEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("lastLoginAt", new TableInfo.Column("lastLoginAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("failedLoginAttempts", new TableInfo.Column("failedLoginAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("lockoutUntil", new TableInfo.Column("lockoutUntil", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.sentinelguard.data.database.entities.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsSecuritySignals = new HashMap<String, TableInfo.Column>(6);
        _columnsSecuritySignals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecuritySignals.put("signalType", new TableInfo.Column("signalType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecuritySignals.put("value", new TableInfo.Column("value", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecuritySignals.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecuritySignals.put("metadata", new TableInfo.Column("metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSecuritySignals.put("processed", new TableInfo.Column("processed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSecuritySignals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSecuritySignals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSecuritySignals = new TableInfo("security_signals", _columnsSecuritySignals, _foreignKeysSecuritySignals, _indicesSecuritySignals);
        final TableInfo _existingSecuritySignals = TableInfo.read(db, "security_signals");
        if (!_infoSecuritySignals.equals(_existingSecuritySignals)) {
          return new RoomOpenHelper.ValidationResult(false, "security_signals(com.sentinelguard.data.database.entities.SecuritySignalEntity).\n"
                  + " Expected:\n" + _infoSecuritySignals + "\n"
                  + " Found:\n" + _existingSecuritySignals);
        }
        final HashMap<String, TableInfo.Column> _columnsBehavioralBaselines = new HashMap<String, TableInfo.Column>(9);
        _columnsBehavioralBaselines.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("metricType", new TableInfo.Column("metricType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("baselineValue", new TableInfo.Column("baselineValue", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("variance", new TableInfo.Column("variance", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("sampleCount", new TableInfo.Column("sampleCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralBaselines.put("learningComplete", new TableInfo.Column("learningComplete", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBehavioralBaselines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBehavioralBaselines = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBehavioralBaselines = new TableInfo("behavioral_baselines", _columnsBehavioralBaselines, _foreignKeysBehavioralBaselines, _indicesBehavioralBaselines);
        final TableInfo _existingBehavioralBaselines = TableInfo.read(db, "behavioral_baselines");
        if (!_infoBehavioralBaselines.equals(_existingBehavioralBaselines)) {
          return new RoomOpenHelper.ValidationResult(false, "behavioral_baselines(com.sentinelguard.data.database.entities.BehavioralBaselineEntity).\n"
                  + " Expected:\n" + _infoBehavioralBaselines + "\n"
                  + " Found:\n" + _existingBehavioralBaselines);
        }
        final HashMap<String, TableInfo.Column> _columnsRiskScores = new HashMap<String, TableInfo.Column>(9);
        _columnsRiskScores.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("totalScore", new TableInfo.Column("totalScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("riskLevel", new TableInfo.Column("riskLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("signalContributions", new TableInfo.Column("signalContributions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("triggeredAction", new TableInfo.Column("triggeredAction", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("triggerReason", new TableInfo.Column("triggerReason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("decayed", new TableInfo.Column("decayed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskScores.put("currentScore", new TableInfo.Column("currentScore", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRiskScores = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRiskScores = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRiskScores = new TableInfo("risk_scores", _columnsRiskScores, _foreignKeysRiskScores, _indicesRiskScores);
        final TableInfo _existingRiskScores = TableInfo.read(db, "risk_scores");
        if (!_infoRiskScores.equals(_existingRiskScores)) {
          return new RoomOpenHelper.ValidationResult(false, "risk_scores(com.sentinelguard.data.database.entities.RiskScoreEntity).\n"
                  + " Expected:\n" + _infoRiskScores + "\n"
                  + " Found:\n" + _existingRiskScores);
        }
        final HashMap<String, TableInfo.Column> _columnsIncidents = new HashMap<String, TableInfo.Column>(11);
        _columnsIncidents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("riskScore", new TableInfo.Column("riskScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("triggeredBy", new TableInfo.Column("triggeredBy", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("actionsTaken", new TableInfo.Column("actionsTaken", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("location", new TableInfo.Column("location", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("deviceState", new TableInfo.Column("deviceState", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("resolved", new TableInfo.Column("resolved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("resolvedAt", new TableInfo.Column("resolvedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncidents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncidents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncidents = new TableInfo("incidents", _columnsIncidents, _foreignKeysIncidents, _indicesIncidents);
        final TableInfo _existingIncidents = TableInfo.read(db, "incidents");
        if (!_infoIncidents.equals(_existingIncidents)) {
          return new RoomOpenHelper.ValidationResult(false, "incidents(com.sentinelguard.data.database.entities.IncidentEntity).\n"
                  + " Expected:\n" + _infoIncidents + "\n"
                  + " Found:\n" + _existingIncidents);
        }
        final HashMap<String, TableInfo.Column> _columnsAlertHistory = new HashMap<String, TableInfo.Column>(11);
        _columnsAlertHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("recipientEmail", new TableInfo.Column("recipientEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("subject", new TableInfo.Column("subject", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("incidentId", new TableInfo.Column("incidentId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("sentAt", new TableInfo.Column("sentAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("lastError", new TableInfo.Column("lastError", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertHistory.put("nextRetryAt", new TableInfo.Column("nextRetryAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlertHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlertHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlertHistory = new TableInfo("alert_history", _columnsAlertHistory, _foreignKeysAlertHistory, _indicesAlertHistory);
        final TableInfo _existingAlertHistory = TableInfo.read(db, "alert_history");
        if (!_infoAlertHistory.equals(_existingAlertHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "alert_history(com.sentinelguard.data.database.entities.AlertHistoryEntity).\n"
                  + " Expected:\n" + _infoAlertHistory + "\n"
                  + " Found:\n" + _existingAlertHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsAlertQueue = new HashMap<String, TableInfo.Column>(11);
        _columnsAlertQueue.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("recipientEmail", new TableInfo.Column("recipientEmail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("subject", new TableInfo.Column("subject", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("incidentId", new TableInfo.Column("incidentId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("lastError", new TableInfo.Column("lastError", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("nextRetryAt", new TableInfo.Column("nextRetryAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertQueue.put("sentAt", new TableInfo.Column("sentAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlertQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlertQueue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlertQueue = new TableInfo("alert_queue", _columnsAlertQueue, _foreignKeysAlertQueue, _indicesAlertQueue);
        final TableInfo _existingAlertQueue = TableInfo.read(db, "alert_queue");
        if (!_infoAlertQueue.equals(_existingAlertQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "alert_queue(com.sentinelguard.data.database.entities.AlertQueueEntity).\n"
                  + " Expected:\n" + _infoAlertQueue + "\n"
                  + " Found:\n" + _existingAlertQueue);
        }
        final HashMap<String, TableInfo.Column> _columnsAppUsagePatterns = new HashMap<String, TableInfo.Column>(8);
        _columnsAppUsagePatterns.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("hourOfDay", new TableInfo.Column("hourOfDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("dayOfWeek", new TableInfo.Column("dayOfWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("usageCount", new TableInfo.Column("usageCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("avgDurationMs", new TableInfo.Column("avgDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("lastUsed", new TableInfo.Column("lastUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppUsagePatterns.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppUsagePatterns = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppUsagePatterns = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAppUsagePatterns = new TableInfo("app_usage_patterns", _columnsAppUsagePatterns, _foreignKeysAppUsagePatterns, _indicesAppUsagePatterns);
        final TableInfo _existingAppUsagePatterns = TableInfo.read(db, "app_usage_patterns");
        if (!_infoAppUsagePatterns.equals(_existingAppUsagePatterns)) {
          return new RoomOpenHelper.ValidationResult(false, "app_usage_patterns(com.sentinelguard.data.database.entities.AppUsagePatternEntity).\n"
                  + " Expected:\n" + _infoAppUsagePatterns + "\n"
                  + " Found:\n" + _existingAppUsagePatterns);
        }
        final HashMap<String, TableInfo.Column> _columnsLocationClusters = new HashMap<String, TableInfo.Column>(10);
        _columnsLocationClusters.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("centerLatitude", new TableInfo.Column("centerLatitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("centerLongitude", new TableInfo.Column("centerLongitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("radiusMeters", new TableInfo.Column("radiusMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("visitCount", new TableInfo.Column("visitCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("totalTimeSpentMs", new TableInfo.Column("totalTimeSpentMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("lastVisited", new TableInfo.Column("lastVisited", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("isTrusted", new TableInfo.Column("isTrusted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationClusters.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationClusters = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocationClusters = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocationClusters = new TableInfo("location_clusters", _columnsLocationClusters, _foreignKeysLocationClusters, _indicesLocationClusters);
        final TableInfo _existingLocationClusters = TableInfo.read(db, "location_clusters");
        if (!_infoLocationClusters.equals(_existingLocationClusters)) {
          return new RoomOpenHelper.ValidationResult(false, "location_clusters(com.sentinelguard.data.database.entities.LocationClusterEntity).\n"
                  + " Expected:\n" + _infoLocationClusters + "\n"
                  + " Found:\n" + _existingLocationClusters);
        }
        final HashMap<String, TableInfo.Column> _columnsKnownNetworks = new HashMap<String, TableInfo.Column>(8);
        _columnsKnownNetworks.put("ssid", new TableInfo.Column("ssid", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("bssid", new TableInfo.Column("bssid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("isSecure", new TableInfo.Column("isSecure", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("isTrusted", new TableInfo.Column("isTrusted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("connectionCount", new TableInfo.Column("connectionCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("lastConnected", new TableInfo.Column("lastConnected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("totalTimeConnectedMs", new TableInfo.Column("totalTimeConnectedMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownNetworks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysKnownNetworks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesKnownNetworks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoKnownNetworks = new TableInfo("known_networks", _columnsKnownNetworks, _foreignKeysKnownNetworks, _indicesKnownNetworks);
        final TableInfo _existingKnownNetworks = TableInfo.read(db, "known_networks");
        if (!_infoKnownNetworks.equals(_existingKnownNetworks)) {
          return new RoomOpenHelper.ValidationResult(false, "known_networks(com.sentinelguard.data.database.entities.KnownNetworkEntity).\n"
                  + " Expected:\n" + _infoKnownNetworks + "\n"
                  + " Found:\n" + _existingKnownNetworks);
        }
        final HashMap<String, TableInfo.Column> _columnsUnlockPatterns = new HashMap<String, TableInfo.Column>(7);
        _columnsUnlockPatterns.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("hourOfDay", new TableInfo.Column("hourOfDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("dayOfWeek", new TableInfo.Column("dayOfWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("unlockCount", new TableInfo.Column("unlockCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("failedAttempts", new TableInfo.Column("failedAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("avgSessionLengthMs", new TableInfo.Column("avgSessionLengthMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnlockPatterns.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUnlockPatterns = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUnlockPatterns = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUnlockPatterns = new TableInfo("unlock_patterns", _columnsUnlockPatterns, _foreignKeysUnlockPatterns, _indicesUnlockPatterns);
        final TableInfo _existingUnlockPatterns = TableInfo.read(db, "unlock_patterns");
        if (!_infoUnlockPatterns.equals(_existingUnlockPatterns)) {
          return new RoomOpenHelper.ValidationResult(false, "unlock_patterns(com.sentinelguard.data.database.entities.UnlockPatternEntity).\n"
                  + " Expected:\n" + _infoUnlockPatterns + "\n"
                  + " Found:\n" + _existingUnlockPatterns);
        }
        final HashMap<String, TableInfo.Column> _columnsBehavioralAnomalies = new HashMap<String, TableInfo.Column>(7);
        _columnsBehavioralAnomalies.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("anomalyType", new TableInfo.Column("anomalyType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("severity", new TableInfo.Column("severity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("riskPoints", new TableInfo.Column("riskPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("resolved", new TableInfo.Column("resolved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBehavioralAnomalies.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBehavioralAnomalies = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBehavioralAnomalies = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBehavioralAnomalies = new TableInfo("behavioral_anomalies", _columnsBehavioralAnomalies, _foreignKeysBehavioralAnomalies, _indicesBehavioralAnomalies);
        final TableInfo _existingBehavioralAnomalies = TableInfo.read(db, "behavioral_anomalies");
        if (!_infoBehavioralAnomalies.equals(_existingBehavioralAnomalies)) {
          return new RoomOpenHelper.ValidationResult(false, "behavioral_anomalies(com.sentinelguard.data.database.entities.BehavioralAnomalyEntity).\n"
                  + " Expected:\n" + _infoBehavioralAnomalies + "\n"
                  + " Found:\n" + _existingBehavioralAnomalies);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "30a5d771e733972ac02c679caade4951", "cd7a0d31ff64d1b3757be4af036c6092");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","security_signals","behavioral_baselines","risk_scores","incidents","alert_history","alert_queue","app_usage_patterns","location_clusters","known_networks","unlock_patterns","behavioral_anomalies");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `security_signals`");
      _db.execSQL("DELETE FROM `behavioral_baselines`");
      _db.execSQL("DELETE FROM `risk_scores`");
      _db.execSQL("DELETE FROM `incidents`");
      _db.execSQL("DELETE FROM `alert_history`");
      _db.execSQL("DELETE FROM `alert_queue`");
      _db.execSQL("DELETE FROM `app_usage_patterns`");
      _db.execSQL("DELETE FROM `location_clusters`");
      _db.execSQL("DELETE FROM `known_networks`");
      _db.execSQL("DELETE FROM `unlock_patterns`");
      _db.execSQL("DELETE FROM `behavioral_anomalies`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SecuritySignalDao.class, SecuritySignalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BehavioralBaselineDao.class, BehavioralBaselineDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RiskScoreDao.class, RiskScoreDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncidentDao.class, IncidentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertHistoryDao.class, AlertHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertQueueDao.class, AlertQueueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AppUsagePatternDao.class, AppUsagePatternDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LocationClusterDao.class, LocationClusterDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(KnownNetworkDao.class, KnownNetworkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UnlockPatternDao.class, UnlockPatternDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BehavioralAnomalyDao.class, BehavioralAnomalyDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public SecuritySignalDao securitySignalDao() {
    if (_securitySignalDao != null) {
      return _securitySignalDao;
    } else {
      synchronized(this) {
        if(_securitySignalDao == null) {
          _securitySignalDao = new SecuritySignalDao_Impl(this);
        }
        return _securitySignalDao;
      }
    }
  }

  @Override
  public BehavioralBaselineDao behavioralBaselineDao() {
    if (_behavioralBaselineDao != null) {
      return _behavioralBaselineDao;
    } else {
      synchronized(this) {
        if(_behavioralBaselineDao == null) {
          _behavioralBaselineDao = new BehavioralBaselineDao_Impl(this);
        }
        return _behavioralBaselineDao;
      }
    }
  }

  @Override
  public RiskScoreDao riskScoreDao() {
    if (_riskScoreDao != null) {
      return _riskScoreDao;
    } else {
      synchronized(this) {
        if(_riskScoreDao == null) {
          _riskScoreDao = new RiskScoreDao_Impl(this);
        }
        return _riskScoreDao;
      }
    }
  }

  @Override
  public IncidentDao incidentDao() {
    if (_incidentDao != null) {
      return _incidentDao;
    } else {
      synchronized(this) {
        if(_incidentDao == null) {
          _incidentDao = new IncidentDao_Impl(this);
        }
        return _incidentDao;
      }
    }
  }

  @Override
  public AlertHistoryDao alertHistoryDao() {
    if (_alertHistoryDao != null) {
      return _alertHistoryDao;
    } else {
      synchronized(this) {
        if(_alertHistoryDao == null) {
          _alertHistoryDao = new AlertHistoryDao_Impl(this);
        }
        return _alertHistoryDao;
      }
    }
  }

  @Override
  public AlertQueueDao alertQueueDao() {
    if (_alertQueueDao != null) {
      return _alertQueueDao;
    } else {
      synchronized(this) {
        if(_alertQueueDao == null) {
          _alertQueueDao = new AlertQueueDao_Impl(this);
        }
        return _alertQueueDao;
      }
    }
  }

  @Override
  public AppUsagePatternDao appUsagePatternDao() {
    if (_appUsagePatternDao != null) {
      return _appUsagePatternDao;
    } else {
      synchronized(this) {
        if(_appUsagePatternDao == null) {
          _appUsagePatternDao = new AppUsagePatternDao_Impl(this);
        }
        return _appUsagePatternDao;
      }
    }
  }

  @Override
  public LocationClusterDao locationClusterDao() {
    if (_locationClusterDao != null) {
      return _locationClusterDao;
    } else {
      synchronized(this) {
        if(_locationClusterDao == null) {
          _locationClusterDao = new LocationClusterDao_Impl(this);
        }
        return _locationClusterDao;
      }
    }
  }

  @Override
  public KnownNetworkDao knownNetworkDao() {
    if (_knownNetworkDao != null) {
      return _knownNetworkDao;
    } else {
      synchronized(this) {
        if(_knownNetworkDao == null) {
          _knownNetworkDao = new KnownNetworkDao_Impl(this);
        }
        return _knownNetworkDao;
      }
    }
  }

  @Override
  public UnlockPatternDao unlockPatternDao() {
    if (_unlockPatternDao != null) {
      return _unlockPatternDao;
    } else {
      synchronized(this) {
        if(_unlockPatternDao == null) {
          _unlockPatternDao = new UnlockPatternDao_Impl(this);
        }
        return _unlockPatternDao;
      }
    }
  }

  @Override
  public BehavioralAnomalyDao behavioralAnomalyDao() {
    if (_behavioralAnomalyDao != null) {
      return _behavioralAnomalyDao;
    } else {
      synchronized(this) {
        if(_behavioralAnomalyDao == null) {
          _behavioralAnomalyDao = new BehavioralAnomalyDao_Impl(this);
        }
        return _behavioralAnomalyDao;
      }
    }
  }
}
