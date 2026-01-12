package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.auth.AuthRepository;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
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
public final class SetupViewModel_Factory implements Factory<SetupViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  public SetupViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public SetupViewModel get() {
    return newInstance(authRepositoryProvider.get(), securePrefsProvider.get());
  }

  public static SetupViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    return new SetupViewModel_Factory(authRepositoryProvider, securePrefsProvider);
  }

  public static SetupViewModel newInstance(AuthRepository authRepository,
      SecurePreferencesManager securePrefs) {
    return new SetupViewModel(authRepository, securePrefs);
  }
}
