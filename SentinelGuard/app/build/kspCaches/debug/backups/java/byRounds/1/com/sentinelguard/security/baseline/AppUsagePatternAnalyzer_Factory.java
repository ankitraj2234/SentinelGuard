package com.sentinelguard.security.baseline;

import android.content.Context;
import com.sentinelguard.data.database.dao.AppUsagePatternDao;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.security.collector.AppUsageTracker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppUsagePatternAnalyzer_Factory implements Factory<AppUsagePatternAnalyzer> {
  private final Provider<Context> contextProvider;

  private final Provider<AppUsageTracker> appUsageTrackerProvider;

  private final Provider<AppUsagePatternDao> appUsagePatternDaoProvider;

  private final Provider<BehavioralAnomalyDao> anomalyDaoProvider;

  public AppUsagePatternAnalyzer_Factory(Provider<Context> contextProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<AppUsagePatternDao> appUsagePatternDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    this.contextProvider = contextProvider;
    this.appUsageTrackerProvider = appUsageTrackerProvider;
    this.appUsagePatternDaoProvider = appUsagePatternDaoProvider;
    this.anomalyDaoProvider = anomalyDaoProvider;
  }

  @Override
  public AppUsagePatternAnalyzer get() {
    return newInstance(contextProvider.get(), appUsageTrackerProvider.get(), appUsagePatternDaoProvider.get(), anomalyDaoProvider.get());
  }

  public static AppUsagePatternAnalyzer_Factory create(Provider<Context> contextProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<AppUsagePatternDao> appUsagePatternDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    return new AppUsagePatternAnalyzer_Factory(contextProvider, appUsageTrackerProvider, appUsagePatternDaoProvider, anomalyDaoProvider);
  }

  public static AppUsagePatternAnalyzer newInstance(Context context,
      AppUsageTracker appUsageTracker, AppUsagePatternDao appUsagePatternDao,
      BehavioralAnomalyDao anomalyDao) {
    return new AppUsagePatternAnalyzer(context, appUsageTracker, appUsagePatternDao, anomalyDao);
  }
}
