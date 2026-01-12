package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.data.preferences.SecurePreferencesManager;
import com.sentinelguard.email.EmailService;
import com.sentinelguard.report.ForensicReportGenerator;
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
public final class ForensicReportViewModel_Factory implements Factory<ForensicReportViewModel> {
  private final Provider<ForensicReportGenerator> reportGeneratorProvider;

  private final Provider<EmailService> emailServiceProvider;

  private final Provider<SecurePreferencesManager> securePreferencesManagerProvider;

  public ForensicReportViewModel_Factory(Provider<ForensicReportGenerator> reportGeneratorProvider,
      Provider<EmailService> emailServiceProvider,
      Provider<SecurePreferencesManager> securePreferencesManagerProvider) {
    this.reportGeneratorProvider = reportGeneratorProvider;
    this.emailServiceProvider = emailServiceProvider;
    this.securePreferencesManagerProvider = securePreferencesManagerProvider;
  }

  @Override
  public ForensicReportViewModel get() {
    return newInstance(reportGeneratorProvider.get(), emailServiceProvider.get(), securePreferencesManagerProvider.get());
  }

  public static ForensicReportViewModel_Factory create(
      Provider<ForensicReportGenerator> reportGeneratorProvider,
      Provider<EmailService> emailServiceProvider,
      Provider<SecurePreferencesManager> securePreferencesManagerProvider) {
    return new ForensicReportViewModel_Factory(reportGeneratorProvider, emailServiceProvider, securePreferencesManagerProvider);
  }

  public static ForensicReportViewModel newInstance(ForensicReportGenerator reportGenerator,
      EmailService emailService, SecurePreferencesManager securePreferencesManager) {
    return new ForensicReportViewModel(reportGenerator, emailService, securePreferencesManager);
  }
}
