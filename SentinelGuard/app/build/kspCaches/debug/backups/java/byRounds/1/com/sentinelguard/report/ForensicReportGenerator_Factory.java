package com.sentinelguard.report;

import android.content.Context;
import com.sentinelguard.data.database.dao.CellTowerDao;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.security.alert.SecurityAlertManager;
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
public final class ForensicReportGenerator_Factory implements Factory<ForensicReportGenerator> {
  private final Provider<Context> contextProvider;

  private final Provider<CellTowerDao> cellTowerDaoProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<AppUsageTracker> appUsageTrackerProvider;

  private final Provider<SecurityAlertManager> alertManagerProvider;

  public ForensicReportGenerator_Factory(Provider<Context> contextProvider,
      Provider<CellTowerDao> cellTowerDaoProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<SecurityAlertManager> alertManagerProvider) {
    this.contextProvider = contextProvider;
    this.cellTowerDaoProvider = cellTowerDaoProvider;
    this.securePreferencesProvider = securePreferencesProvider;
    this.appUsageTrackerProvider = appUsageTrackerProvider;
    this.alertManagerProvider = alertManagerProvider;
  }

  @Override
  public ForensicReportGenerator get() {
    return newInstance(contextProvider.get(), cellTowerDaoProvider.get(), securePreferencesProvider.get(), appUsageTrackerProvider.get(), alertManagerProvider.get());
  }

  public static ForensicReportGenerator_Factory create(Provider<Context> contextProvider,
      Provider<CellTowerDao> cellTowerDaoProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<SecurityAlertManager> alertManagerProvider) {
    return new ForensicReportGenerator_Factory(contextProvider, cellTowerDaoProvider, securePreferencesProvider, appUsageTrackerProvider, alertManagerProvider);
  }

  public static ForensicReportGenerator newInstance(Context context, CellTowerDao cellTowerDao,
      SecurePreferences securePreferences, AppUsageTracker appUsageTracker,
      SecurityAlertManager alertManager) {
    return new ForensicReportGenerator(context, cellTowerDao, securePreferences, appUsageTracker, alertManager);
  }
}
