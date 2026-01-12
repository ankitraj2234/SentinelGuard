package com.sentinelguard.service;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.security.alert.SecurityAlertManager;
import com.sentinelguard.security.baseline.BaselineEngine;
import com.sentinelguard.security.collector.AppUsageTracker;
import com.sentinelguard.security.risk.RiskScoringEngine;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MonitoringService_MembersInjector implements MembersInjector<MonitoringService> {
  private final Provider<AppUsageTracker> appUsageTrackerProvider;

  private final Provider<BaselineEngine> baselineEngineProvider;

  private final Provider<RiskScoringEngine> riskScoringEngineProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<SecurityAlertManager> alertManagerProvider;

  public MonitoringService_MembersInjector(Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<SecurityAlertManager> alertManagerProvider) {
    this.appUsageTrackerProvider = appUsageTrackerProvider;
    this.baselineEngineProvider = baselineEngineProvider;
    this.riskScoringEngineProvider = riskScoringEngineProvider;
    this.securePreferencesProvider = securePreferencesProvider;
    this.alertManagerProvider = alertManagerProvider;
  }

  public static MembersInjector<MonitoringService> create(
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<SecurityAlertManager> alertManagerProvider) {
    return new MonitoringService_MembersInjector(appUsageTrackerProvider, baselineEngineProvider, riskScoringEngineProvider, securePreferencesProvider, alertManagerProvider);
  }

  @Override
  public void injectMembers(MonitoringService instance) {
    injectAppUsageTracker(instance, appUsageTrackerProvider.get());
    injectBaselineEngine(instance, baselineEngineProvider.get());
    injectRiskScoringEngine(instance, riskScoringEngineProvider.get());
    injectSecurePreferences(instance, securePreferencesProvider.get());
    injectAlertManager(instance, alertManagerProvider.get());
  }

  @InjectedFieldSignature("com.sentinelguard.service.MonitoringService.appUsageTracker")
  public static void injectAppUsageTracker(MonitoringService instance,
      AppUsageTracker appUsageTracker) {
    instance.appUsageTracker = appUsageTracker;
  }

  @InjectedFieldSignature("com.sentinelguard.service.MonitoringService.baselineEngine")
  public static void injectBaselineEngine(MonitoringService instance,
      BaselineEngine baselineEngine) {
    instance.baselineEngine = baselineEngine;
  }

  @InjectedFieldSignature("com.sentinelguard.service.MonitoringService.riskScoringEngine")
  public static void injectRiskScoringEngine(MonitoringService instance,
      RiskScoringEngine riskScoringEngine) {
    instance.riskScoringEngine = riskScoringEngine;
  }

  @InjectedFieldSignature("com.sentinelguard.service.MonitoringService.securePreferences")
  public static void injectSecurePreferences(MonitoringService instance,
      SecurePreferences securePreferences) {
    instance.securePreferences = securePreferences;
  }

  @InjectedFieldSignature("com.sentinelguard.service.MonitoringService.alertManager")
  public static void injectAlertManager(MonitoringService instance,
      SecurityAlertManager alertManager) {
    instance.alertManager = alertManager;
  }
}
