package com.sentinelguard.security.alert;

import android.content.Context;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.email.EmailService;
import com.sentinelguard.security.intruder.IntruderCaptureService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SecurityAlertManager_Factory implements Factory<SecurityAlertManager> {
  private final Provider<Context> contextProvider;

  private final Provider<EmailService> emailServiceProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<IntruderCaptureService> intruderCaptureServiceProvider;

  public SecurityAlertManager_Factory(Provider<Context> contextProvider,
      Provider<EmailService> emailServiceProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<IntruderCaptureService> intruderCaptureServiceProvider) {
    this.contextProvider = contextProvider;
    this.emailServiceProvider = emailServiceProvider;
    this.securePreferencesProvider = securePreferencesProvider;
    this.intruderCaptureServiceProvider = intruderCaptureServiceProvider;
  }

  @Override
  public SecurityAlertManager get() {
    return newInstance(contextProvider.get(), emailServiceProvider.get(), securePreferencesProvider.get(), intruderCaptureServiceProvider.get());
  }

  public static SecurityAlertManager_Factory create(Provider<Context> contextProvider,
      Provider<EmailService> emailServiceProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<IntruderCaptureService> intruderCaptureServiceProvider) {
    return new SecurityAlertManager_Factory(contextProvider, emailServiceProvider, securePreferencesProvider, intruderCaptureServiceProvider);
  }

  public static SecurityAlertManager newInstance(Context context, EmailService emailService,
      SecurePreferences securePreferences, IntruderCaptureService intruderCaptureService) {
    return new SecurityAlertManager(context, emailService, securePreferences, intruderCaptureService);
  }
}
