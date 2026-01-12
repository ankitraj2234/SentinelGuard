package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.scanner.MalwareScanner;
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
public final class ScanViewModel_Factory implements Factory<ScanViewModel> {
  private final Provider<MalwareScanner> scannerProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public ScanViewModel_Factory(Provider<MalwareScanner> scannerProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.scannerProvider = scannerProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public ScanViewModel get() {
    return newInstance(scannerProvider.get(), securePreferencesProvider.get());
  }

  public static ScanViewModel_Factory create(Provider<MalwareScanner> scannerProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new ScanViewModel_Factory(scannerProvider, securePreferencesProvider);
  }

  public static ScanViewModel newInstance(MalwareScanner scanner,
      SecurePreferences securePreferences) {
    return new ScanViewModel(scanner, securePreferences);
  }
}
