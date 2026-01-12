package com.sentinelguard.security.risk;

import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.domain.repository.RiskScoreRepository;
import com.sentinelguard.domain.repository.SecuritySignalRepository;
import com.sentinelguard.security.baseline.AppUsagePatternAnalyzer;
import com.sentinelguard.security.baseline.BaselineEngine;
import com.sentinelguard.security.baseline.LocationClusterManager;
import com.sentinelguard.security.baseline.UnlockPatternAnalyzer;
import com.sentinelguard.security.collector.NetworkBehaviorTracker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class RiskScoringEngine_Factory implements Factory<RiskScoringEngine> {
  private final Provider<RiskScoreRepository> riskScoreRepositoryProvider;

  private final Provider<SecuritySignalRepository> signalRepositoryProvider;

  private final Provider<BaselineEngine> baselineEngineProvider;

  private final Provider<BehavioralAnomalyDao> anomalyDaoProvider;

  private final Provider<AppUsagePatternAnalyzer> appUsageAnalyzerProvider;

  private final Provider<LocationClusterManager> locationClusterManagerProvider;

  private final Provider<NetworkBehaviorTracker> networkTrackerProvider;

  private final Provider<UnlockPatternAnalyzer> unlockAnalyzerProvider;

  public RiskScoringEngine_Factory(Provider<RiskScoreRepository> riskScoreRepositoryProvider,
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider,
      Provider<AppUsagePatternAnalyzer> appUsageAnalyzerProvider,
      Provider<LocationClusterManager> locationClusterManagerProvider,
      Provider<NetworkBehaviorTracker> networkTrackerProvider,
      Provider<UnlockPatternAnalyzer> unlockAnalyzerProvider) {
    this.riskScoreRepositoryProvider = riskScoreRepositoryProvider;
    this.signalRepositoryProvider = signalRepositoryProvider;
    this.baselineEngineProvider = baselineEngineProvider;
    this.anomalyDaoProvider = anomalyDaoProvider;
    this.appUsageAnalyzerProvider = appUsageAnalyzerProvider;
    this.locationClusterManagerProvider = locationClusterManagerProvider;
    this.networkTrackerProvider = networkTrackerProvider;
    this.unlockAnalyzerProvider = unlockAnalyzerProvider;
  }

  @Override
  public RiskScoringEngine get() {
    return newInstance(riskScoreRepositoryProvider.get(), signalRepositoryProvider.get(), baselineEngineProvider.get(), anomalyDaoProvider.get(), appUsageAnalyzerProvider.get(), locationClusterManagerProvider.get(), networkTrackerProvider.get(), unlockAnalyzerProvider.get());
  }

  public static RiskScoringEngine_Factory create(
      Provider<RiskScoreRepository> riskScoreRepositoryProvider,
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider,
      Provider<AppUsagePatternAnalyzer> appUsageAnalyzerProvider,
      Provider<LocationClusterManager> locationClusterManagerProvider,
      Provider<NetworkBehaviorTracker> networkTrackerProvider,
      Provider<UnlockPatternAnalyzer> unlockAnalyzerProvider) {
    return new RiskScoringEngine_Factory(riskScoreRepositoryProvider, signalRepositoryProvider, baselineEngineProvider, anomalyDaoProvider, appUsageAnalyzerProvider, locationClusterManagerProvider, networkTrackerProvider, unlockAnalyzerProvider);
  }

  public static RiskScoringEngine newInstance(RiskScoreRepository riskScoreRepository,
      SecuritySignalRepository signalRepository, BaselineEngine baselineEngine,
      BehavioralAnomalyDao anomalyDao, AppUsagePatternAnalyzer appUsageAnalyzer,
      LocationClusterManager locationClusterManager, NetworkBehaviorTracker networkTracker,
      UnlockPatternAnalyzer unlockAnalyzer) {
    return new RiskScoringEngine(riskScoreRepository, signalRepository, baselineEngine, anomalyDao, appUsageAnalyzer, locationClusterManager, networkTracker, unlockAnalyzer);
  }
}
