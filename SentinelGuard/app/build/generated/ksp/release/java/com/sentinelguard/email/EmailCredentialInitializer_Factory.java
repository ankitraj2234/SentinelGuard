package com.sentinelguard.email;

import android.content.Context;
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
public final class EmailCredentialInitializer_Factory implements Factory<EmailCredentialInitializer> {
  private final Provider<Context> contextProvider;

  private final Provider<EmailService> emailServiceProvider;

  public EmailCredentialInitializer_Factory(Provider<Context> contextProvider,
      Provider<EmailService> emailServiceProvider) {
    this.contextProvider = contextProvider;
    this.emailServiceProvider = emailServiceProvider;
  }

  @Override
  public EmailCredentialInitializer get() {
    return newInstance(contextProvider.get(), emailServiceProvider.get());
  }

  public static EmailCredentialInitializer_Factory create(Provider<Context> contextProvider,
      Provider<EmailService> emailServiceProvider) {
    return new EmailCredentialInitializer_Factory(contextProvider, emailServiceProvider);
  }

  public static EmailCredentialInitializer newInstance(Context context, EmailService emailService) {
    return new EmailCredentialInitializer(context, emailService);
  }
}
