package com.sentinelguard.di;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.domain.repository.AlertQueueRepository;
import com.sentinelguard.domain.repository.IncidentRepository;
import com.sentinelguard.security.response.AppLockManager;
import com.sentinelguard.security.response.ResponseEngine;
import com.sentinelguard.security.response.SessionManager;
import com.sentinelguard.security.risk.RiskScoringEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ResponseModule_ProvideResponseEngineFactory implements Factory<ResponseEngine> {
  private final Provider<RiskScoringEngine> riskScoringEngineProvider;

  private final Provider<AppLockManager> appLockManagerProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<IncidentRepository> incidentRepositoryProvider;

  private final Provider<AlertQueueRepository> alertQueueRepositoryProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public ResponseModule_ProvideResponseEngineFactory(
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<AppLockManager> appLockManagerProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<IncidentRepository> incidentRepositoryProvider,
      Provider<AlertQueueRepository> alertQueueRepositoryProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.riskScoringEngineProvider = riskScoringEngineProvider;
    this.appLockManagerProvider = appLockManagerProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.incidentRepositoryProvider = incidentRepositoryProvider;
    this.alertQueueRepositoryProvider = alertQueueRepositoryProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public ResponseEngine get() {
    return provideResponseEngine(riskScoringEngineProvider.get(), appLockManagerProvider.get(), sessionManagerProvider.get(), incidentRepositoryProvider.get(), alertQueueRepositoryProvider.get(), securePreferencesProvider.get());
  }

  public static ResponseModule_ProvideResponseEngineFactory create(
      Provider<RiskScoringEngine> riskScoringEngineProvider,
      Provider<AppLockManager> appLockManagerProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<IncidentRepository> incidentRepositoryProvider,
      Provider<AlertQueueRepository> alertQueueRepositoryProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new ResponseModule_ProvideResponseEngineFactory(riskScoringEngineProvider, appLockManagerProvider, sessionManagerProvider, incidentRepositoryProvider, alertQueueRepositoryProvider, securePreferencesProvider);
  }

  public static ResponseEngine provideResponseEngine(RiskScoringEngine riskScoringEngine,
      AppLockManager appLockManager, SessionManager sessionManager,
      IncidentRepository incidentRepository, AlertQueueRepository alertQueueRepository,
      SecurePreferences securePreferences) {
    return Preconditions.checkNotNullFromProvides(ResponseModule.INSTANCE.provideResponseEngine(riskScoringEngine, appLockManager, sessionManager, incidentRepository, alertQueueRepository, securePreferences));
  }
}
