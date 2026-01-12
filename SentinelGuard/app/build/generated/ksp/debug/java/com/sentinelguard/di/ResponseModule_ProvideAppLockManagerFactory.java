package com.sentinelguard.di;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.security.response.AppLockManager;
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
public final class ResponseModule_ProvideAppLockManagerFactory implements Factory<AppLockManager> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  public ResponseModule_ProvideAppLockManagerFactory(
      Provider<SecurePreferences> securePreferencesProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public AppLockManager get() {
    return provideAppLockManager(securePreferencesProvider.get());
  }

  public static ResponseModule_ProvideAppLockManagerFactory create(
      Provider<SecurePreferences> securePreferencesProvider) {
    return new ResponseModule_ProvideAppLockManagerFactory(securePreferencesProvider);
  }

  public static AppLockManager provideAppLockManager(SecurePreferences securePreferences) {
    return Preconditions.checkNotNullFromProvides(ResponseModule.INSTANCE.provideAppLockManager(securePreferences));
  }
}
