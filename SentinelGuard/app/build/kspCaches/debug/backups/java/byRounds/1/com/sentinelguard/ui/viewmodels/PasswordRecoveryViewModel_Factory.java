package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.auth.AuthRepository;
import com.sentinelguard.email.EmailService;
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
public final class PasswordRecoveryViewModel_Factory implements Factory<PasswordRecoveryViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<EmailService> emailServiceProvider;

  public PasswordRecoveryViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<EmailService> emailServiceProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.emailServiceProvider = emailServiceProvider;
  }

  @Override
  public PasswordRecoveryViewModel get() {
    return newInstance(authRepositoryProvider.get(), emailServiceProvider.get());
  }

  public static PasswordRecoveryViewModel_Factory create(
      Provider<AuthRepository> authRepositoryProvider,
      Provider<EmailService> emailServiceProvider) {
    return new PasswordRecoveryViewModel_Factory(authRepositoryProvider, emailServiceProvider);
  }

  public static PasswordRecoveryViewModel newInstance(AuthRepository authRepository,
      EmailService emailService) {
    return new PasswordRecoveryViewModel(authRepository, emailService);
  }
}
