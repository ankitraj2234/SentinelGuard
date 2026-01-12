package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.data.preferences.SecurePreferencesManager;
import com.sentinelguard.security.baseline.BaselineEngine;
import com.sentinelguard.security.risk.RiskScoringEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<RiskScoringEngine> riskScoringEngineProvider;

  private final Provider<BaselineEngine> baselineEngineProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  public DashboardViewModel_Factory(Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    this.riskScoringEngineProvider = riskScoringEngineProvider;
    this.baselineEngineProvider = baselineEngineProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(riskScoringEngineProvider.get(), baselineEngineProvider.get(), securePrefsProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    return new DashboardViewModel_Factory(riskScoringEngineProvider, baselineEngineProvider, securePrefsProvider);
  }

  public static DashboardViewModel newInstance(RiskScoringEngine riskScoringEngine,
      BaselineEngine baselineEngine, SecurePreferencesManager securePrefs) {
    return new DashboardViewModel(riskScoringEngine, baselineEngine, securePrefs);
  }
}
