package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.backup.SettingsBackupManager;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
import com.sentinelguard.domain.repository.UserRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<SecurePreferencesManager> securePrefsManagerProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SettingsBackupManager> backupManagerProvider;

  public SettingsViewModel_Factory(Provider<SecurePreferences> securePreferencesProvider,
      Provider<SecurePreferencesManager> securePrefsManagerProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SettingsBackupManager> backupManagerProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
    this.securePrefsManagerProvider = securePrefsManagerProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.backupManagerProvider = backupManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(securePreferencesProvider.get(), securePrefsManagerProvider.get(), userRepositoryProvider.get(), backupManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<SecurePreferencesManager> securePrefsManagerProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SettingsBackupManager> backupManagerProvider) {
    return new SettingsViewModel_Factory(securePreferencesProvider, securePrefsManagerProvider, userRepositoryProvider, backupManagerProvider);
  }

  public static SettingsViewModel newInstance(SecurePreferences securePreferences,
      SecurePreferencesManager securePrefsManager, UserRepository userRepository,
      SettingsBackupManager backupManager) {
    return new SettingsViewModel(securePreferences, securePrefsManager, userRepository, backupManager);
  }
}
