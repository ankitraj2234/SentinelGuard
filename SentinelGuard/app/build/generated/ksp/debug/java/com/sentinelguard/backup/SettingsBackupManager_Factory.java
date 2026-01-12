package com.sentinelguard.backup;

import android.content.Context;
import com.sentinelguard.data.local.preferences.SecurePreferences;
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
public final class SettingsBackupManager_Factory implements Factory<SettingsBackupManager> {
  private final Provider<Context> contextProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public SettingsBackupManager_Factory(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.contextProvider = contextProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public SettingsBackupManager get() {
    return newInstance(contextProvider.get(), securePreferencesProvider.get());
  }

  public static SettingsBackupManager_Factory create(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new SettingsBackupManager_Factory(contextProvider, securePreferencesProvider);
  }

  public static SettingsBackupManager newInstance(Context context,
      SecurePreferences securePreferences) {
    return new SettingsBackupManager(context, securePreferences);
  }
}
