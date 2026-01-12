package com.sentinelguard.di;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.sentinelguard.data.database.dao.SecuritySignalDao;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
import com.sentinelguard.security.collector.SignalCollector;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class SecurityModule_ProvideSignalCollectorFactory implements Factory<SignalCollector> {
  private final Provider<Context> contextProvider;

  private final Provider<SecuritySignalDao> securitySignalDaoProvider;

  private final Provider<SecurePreferencesManager> securePrefsManagerProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public SecurityModule_ProvideSignalCollectorFactory(Provider<Context> contextProvider,
      Provider<SecuritySignalDao> securitySignalDaoProvider,
      Provider<SecurePreferencesManager> securePrefsManagerProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.contextProvider = contextProvider;
    this.securitySignalDaoProvider = securitySignalDaoProvider;
    this.securePrefsManagerProvider = securePrefsManagerProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  @Override
  public SignalCollector get() {
    return provideSignalCollector(contextProvider.get(), securitySignalDaoProvider.get(), securePrefsManagerProvider.get(), fusedLocationClientProvider.get());
  }

  public static SecurityModule_ProvideSignalCollectorFactory create(
      Provider<Context> contextProvider, Provider<SecuritySignalDao> securitySignalDaoProvider,
      Provider<SecurePreferencesManager> securePrefsManagerProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new SecurityModule_ProvideSignalCollectorFactory(contextProvider, securitySignalDaoProvider, securePrefsManagerProvider, fusedLocationClientProvider);
  }

  public static SignalCollector provideSignalCollector(Context context,
      SecuritySignalDao securitySignalDao, SecurePreferencesManager securePrefsManager,
      FusedLocationProviderClient fusedLocationClient) {
    return Preconditions.checkNotNullFromProvides(SecurityModule.INSTANCE.provideSignalCollector(context, securitySignalDao, securePrefsManager, fusedLocationClient));
  }
}
