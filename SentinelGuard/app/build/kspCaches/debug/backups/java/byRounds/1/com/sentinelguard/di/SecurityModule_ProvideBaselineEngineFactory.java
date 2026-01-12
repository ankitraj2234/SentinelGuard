package com.sentinelguard.di;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.domain.repository.BaselineRepository;
import com.sentinelguard.domain.repository.SecuritySignalRepository;
import com.sentinelguard.security.baseline.BaselineEngine;
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
public final class SecurityModule_ProvideBaselineEngineFactory implements Factory<BaselineEngine> {
  private final Provider<BaselineRepository> baselineRepositoryProvider;

  private final Provider<SecuritySignalRepository> signalRepositoryProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public SecurityModule_ProvideBaselineEngineFactory(
      Provider<BaselineRepository> baselineRepositoryProvider,
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.baselineRepositoryProvider = baselineRepositoryProvider;
    this.signalRepositoryProvider = signalRepositoryProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public BaselineEngine get() {
    return provideBaselineEngine(baselineRepositoryProvider.get(), signalRepositoryProvider.get(), securePreferencesProvider.get());
  }

  public static SecurityModule_ProvideBaselineEngineFactory create(
      Provider<BaselineRepository> baselineRepositoryProvider,
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new SecurityModule_ProvideBaselineEngineFactory(baselineRepositoryProvider, signalRepositoryProvider, securePreferencesProvider);
  }

  public static BaselineEngine provideBaselineEngine(BaselineRepository baselineRepository,
      SecuritySignalRepository signalRepository, SecurePreferences securePreferences) {
    return Preconditions.checkNotNullFromProvides(SecurityModule.INSTANCE.provideBaselineEngine(baselineRepository, signalRepository, securePreferences));
  }
}
