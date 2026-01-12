package com.sentinelguard.di;

import android.content.Context;
import com.sentinelguard.data.local.preferences.SecurePreferences;
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
public final class SecurityModule_ProvideSecurePreferencesFactory implements Factory<SecurePreferences> {
  private final Provider<Context> contextProvider;

  public SecurityModule_ProvideSecurePreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SecurePreferences get() {
    return provideSecurePreferences(contextProvider.get());
  }

  public static SecurityModule_ProvideSecurePreferencesFactory create(
      Provider<Context> contextProvider) {
    return new SecurityModule_ProvideSecurePreferencesFactory(contextProvider);
  }

  public static SecurePreferences provideSecurePreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(SecurityModule.INSTANCE.provideSecurePreferences(context));
  }
}
