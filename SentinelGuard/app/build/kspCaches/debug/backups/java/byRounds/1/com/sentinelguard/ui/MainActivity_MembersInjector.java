package com.sentinelguard.ui;

import com.sentinelguard.auth.BiometricAuthManager;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  public MainActivity_MembersInjector(Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    return new MainActivity_MembersInjector(biometricAuthManagerProvider, securePrefsProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectBiometricAuthManager(instance, biometricAuthManagerProvider.get());
    injectSecurePrefs(instance, securePrefsProvider.get());
  }

  @InjectedFieldSignature("com.sentinelguard.ui.MainActivity.biometricAuthManager")
  public static void injectBiometricAuthManager(MainActivity instance,
      BiometricAuthManager biometricAuthManager) {
    instance.biometricAuthManager = biometricAuthManager;
  }

  @InjectedFieldSignature("com.sentinelguard.ui.MainActivity.securePrefs")
  public static void injectSecurePrefs(MainActivity instance,
      SecurePreferencesManager securePrefs) {
    instance.securePrefs = securePrefs;
  }
}
