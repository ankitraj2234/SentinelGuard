package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.auth.AuthRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<RiskScoringEngine> riskScoringEngineProvider;

  private final Provider<BaselineEngine> baselineEngineProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  public MainViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.riskScoringEngineProvider = riskScoringEngineProvider;
    this.baselineEngineProvider = baselineEngineProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(authRepositoryProvider.get(), riskScoringEngineProvider.get(), baselineEngineProvider.get(), securePrefsProvider.get());
  }

  public static MainViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<BaselineEngine> baselineEngineProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    return new MainViewModel_Factory(authRepositoryProvider, riskScoringEngineProvider, baselineEngineProvider, securePrefsProvider);
  }

  public static MainViewModel newInstance(AuthRepository authRepository,
      RiskScoringEngine riskScoringEngine, BaselineEngine baselineEngine,
      SecurePreferencesManager securePrefs) {
    return new MainViewModel(authRepository, riskScoringEngine, baselineEngine, securePrefs);
  }
}
