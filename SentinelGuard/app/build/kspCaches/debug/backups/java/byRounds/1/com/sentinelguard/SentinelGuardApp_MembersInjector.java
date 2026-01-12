package com.sentinelguard;

import com.sentinelguard.crash.CrashHandler;
import com.sentinelguard.email.EmailCredentialInitializer;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SentinelGuardApp_MembersInjector implements MembersInjector<SentinelGuardApp> {
  private final Provider<EmailCredentialInitializer> emailCredentialInitializerProvider;

  private final Provider<CrashHandler> crashHandlerProvider;

  public SentinelGuardApp_MembersInjector(
      Provider<EmailCredentialInitializer> emailCredentialInitializerProvider,
      Provider<CrashHandler> crashHandlerProvider) {
    this.emailCredentialInitializerProvider = emailCredentialInitializerProvider;
    this.crashHandlerProvider = crashHandlerProvider;
  }

  public static MembersInjector<SentinelGuardApp> create(
      Provider<EmailCredentialInitializer> emailCredentialInitializerProvider,
      Provider<CrashHandler> crashHandlerProvider) {
    return new SentinelGuardApp_MembersInjector(emailCredentialInitializerProvider, crashHandlerProvider);
  }

  @Override
  public void injectMembers(SentinelGuardApp instance) {
    injectEmailCredentialInitializer(instance, emailCredentialInitializerProvider.get());
    injectCrashHandler(instance, crashHandlerProvider.get());
  }

  @InjectedFieldSignature("com.sentinelguard.SentinelGuardApp.emailCredentialInitializer")
  public static void injectEmailCredentialInitializer(SentinelGuardApp instance,
      EmailCredentialInitializer emailCredentialInitializer) {
    instance.emailCredentialInitializer = emailCredentialInitializer;
  }

  @InjectedFieldSignature("com.sentinelguard.SentinelGuardApp.crashHandler")
  public static void injectCrashHandler(SentinelGuardApp instance, CrashHandler crashHandler) {
    instance.crashHandler = crashHandler;
  }
}
