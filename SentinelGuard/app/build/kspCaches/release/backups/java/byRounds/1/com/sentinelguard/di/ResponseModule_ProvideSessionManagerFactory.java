package com.sentinelguard.di;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.security.response.SessionManager;
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
public final class ResponseModule_ProvideSessionManagerFactory implements Factory<SessionManager> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  public ResponseModule_ProvideSessionManagerFactory(
      Provider<SecurePreferences> securePreferencesProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public SessionManager get() {
    return provideSessionManager(securePreferencesProvider.get());
  }

  public static ResponseModule_ProvideSessionManagerFactory create(
      Provider<SecurePreferences> securePreferencesProvider) {
    return new ResponseModule_ProvideSessionManagerFactory(securePreferencesProvider);
  }

  public static SessionManager provideSessionManager(SecurePreferences securePreferences) {
    return Preconditions.checkNotNullFromProvides(ResponseModule.INSTANCE.provideSessionManager(securePreferences));
  }
}
