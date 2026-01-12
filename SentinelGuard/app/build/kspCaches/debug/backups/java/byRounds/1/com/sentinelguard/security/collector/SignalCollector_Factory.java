package com.sentinelguard.security.collector;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.sentinelguard.data.database.dao.SecuritySignalDao;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
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
public final class SignalCollector_Factory implements Factory<SignalCollector> {
  private final Provider<Context> contextProvider;

  private final Provider<SecuritySignalDao> securitySignalDaoProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public SignalCollector_Factory(Provider<Context> contextProvider,
      Provider<SecuritySignalDao> securitySignalDaoProvider,
      Provider<SecurePreferencesManager> securePrefsProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.contextProvider = contextProvider;
    this.securitySignalDaoProvider = securitySignalDaoProvider;
    this.securePrefsProvider = securePrefsProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  @Override
  public SignalCollector get() {
    return newInstance(contextProvider.get(), securitySignalDaoProvider.get(), securePrefsProvider.get(), fusedLocationClientProvider.get());
  }

  public static SignalCollector_Factory create(Provider<Context> contextProvider,
      Provider<SecuritySignalDao> securitySignalDaoProvider,
      Provider<SecurePreferencesManager> securePrefsProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new SignalCollector_Factory(contextProvider, securitySignalDaoProvider, securePrefsProvider, fusedLocationClientProvider);
  }

  public static SignalCollector newInstance(Context context, SecuritySignalDao securitySignalDao,
      SecurePreferencesManager securePrefs, FusedLocationProviderClient fusedLocationClient) {
    return new SignalCollector(context, securitySignalDao, securePrefs, fusedLocationClient);
  }
}
