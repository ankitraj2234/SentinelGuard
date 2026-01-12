package com.sentinelguard.scanner;

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
public final class DeepScanEngine_Factory implements Factory<DeepScanEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<MalwareScanner> malwareScannerProvider;

  private final Provider<FileSystemScanner> fileSystemScannerProvider;

  private final Provider<SystemIntegrityScanner> systemIntegrityScannerProvider;

  private final Provider<NetworkSecurityScanner> networkSecurityScannerProvider;

  private final Provider<PrivacyScanner> privacyScannerProvider;

  public DeepScanEngine_Factory(Provider<Context> contextProvider,
      Provider<MalwareScanner> malwareScannerProvider,
      Provider<FileSystemScanner> fileSystemScannerProvider,
      Provider<SystemIntegrityScanner> systemIntegrityScannerProvider,
      Provider<NetworkSecurityScanner> networkSecurityScannerProvider,
      Provider<PrivacyScanner> privacyScannerProvider) {
    this.contextProvider = contextProvider;
    this.malwareScannerProvider = malwareScannerProvider;
    this.fileSystemScannerProvider = fileSystemScannerProvider;
    this.systemIntegrityScannerProvider = systemIntegrityScannerProvider;
    this.networkSecurityScannerProvider = networkSecurityScannerProvider;
    this.privacyScannerProvider = privacyScannerProvider;
  }

  @Override
  public DeepScanEngine get() {
    return newInstance(contextProvider.get(), malwareScannerProvider.get(), fileSystemScannerProvider.get(), systemIntegrityScannerProvider.get(), networkSecurityScannerProvider.get(), privacyScannerProvider.get());
  }

  public static DeepScanEngine_Factory create(Provider<Context> contextProvider,
      Provider<MalwareScanner> malwareScannerProvider,
      Provider<FileSystemScanner> fileSystemScannerProvider,
      Provider<SystemIntegrityScanner> systemIntegrityScannerProvider,
      Provider<NetworkSecurityScanner> networkSecurityScannerProvider,
      Provider<PrivacyScanner> privacyScannerProvider) {
    return new DeepScanEngine_Factory(contextProvider, malwareScannerProvider, fileSystemScannerProvider, systemIntegrityScannerProvider, networkSecurityScannerProvider, privacyScannerProvider);
  }

  public static DeepScanEngine newInstance(Context context, MalwareScanner malwareScanner,
      FileSystemScanner fileSystemScanner, SystemIntegrityScanner systemIntegrityScanner,
      NetworkSecurityScanner networkSecurityScanner, PrivacyScanner privacyScanner) {
    return new DeepScanEngine(context, malwareScanner, fileSystemScanner, systemIntegrityScanner, networkSecurityScanner, privacyScanner);
  }
}
