package com.sentinelguard.di;

import android.content.Context;
import com.sentinelguard.auth.BiometricAuthManager;
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
public final class AuthModule_ProvideBiometricAuthManagerFactory implements Factory<BiometricAuthManager> {
  private final Provider<Context> contextProvider;

  public AuthModule_ProvideBiometricAuthManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BiometricAuthManager get() {
    return provideBiometricAuthManager(contextProvider.get());
  }

  public static AuthModule_ProvideBiometricAuthManagerFactory create(
      Provider<Context> contextProvider) {
    return new AuthModule_ProvideBiometricAuthManagerFactory(contextProvider);
  }

  public static BiometricAuthManager provideBiometricAuthManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AuthModule.INSTANCE.provideBiometricAuthManager(context));
  }
}
