package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.auth.AuthRepository;
import com.sentinelguard.auth.BiometricAuthManager;
import com.sentinelguard.security.collector.SignalCollector;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  private final Provider<SignalCollector> signalCollectorProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SignalCollector> signalCollectorProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
    this.signalCollectorProvider = signalCollectorProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get(), biometricAuthManagerProvider.get(), signalCollectorProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SignalCollector> signalCollectorProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider, biometricAuthManagerProvider, signalCollectorProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository,
      BiometricAuthManager biometricAuthManager, SignalCollector signalCollector) {
    return new LoginViewModel(authRepository, biometricAuthManager, signalCollector);
  }
}
