package com.sentinelguard.di;

import android.content.Context;
import com.sentinelguard.alert.EmailAlertService;
import com.sentinelguard.data.database.dao.AlertHistoryDao;
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
public final class AlertModule_ProvideEmailAlertServiceFactory implements Factory<EmailAlertService> {
  private final Provider<Context> contextProvider;

  private final Provider<AlertHistoryDao> alertHistoryDaoProvider;

  public AlertModule_ProvideEmailAlertServiceFactory(Provider<Context> contextProvider,
      Provider<AlertHistoryDao> alertHistoryDaoProvider) {
    this.contextProvider = contextProvider;
    this.alertHistoryDaoProvider = alertHistoryDaoProvider;
  }

  @Override
  public EmailAlertService get() {
    return provideEmailAlertService(contextProvider.get(), alertHistoryDaoProvider.get());
  }

  public static AlertModule_ProvideEmailAlertServiceFactory create(
      Provider<Context> contextProvider, Provider<AlertHistoryDao> alertHistoryDaoProvider) {
    return new AlertModule_ProvideEmailAlertServiceFactory(contextProvider, alertHistoryDaoProvider);
  }

  public static EmailAlertService provideEmailAlertService(Context context,
      AlertHistoryDao alertHistoryDao) {
    return Preconditions.checkNotNullFromProvides(AlertModule.INSTANCE.provideEmailAlertService(context, alertHistoryDao));
  }
}
