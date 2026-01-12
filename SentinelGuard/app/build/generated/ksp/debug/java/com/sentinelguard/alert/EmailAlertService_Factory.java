package com.sentinelguard.alert;

import android.content.Context;
import com.sentinelguard.data.database.dao.AlertHistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class EmailAlertService_Factory implements Factory<EmailAlertService> {
  private final Provider<Context> contextProvider;

  private final Provider<AlertHistoryDao> alertHistoryDaoProvider;

  public EmailAlertService_Factory(Provider<Context> contextProvider,
      Provider<AlertHistoryDao> alertHistoryDaoProvider) {
    this.contextProvider = contextProvider;
    this.alertHistoryDaoProvider = alertHistoryDaoProvider;
  }

  @Override
  public EmailAlertService get() {
    return newInstance(contextProvider.get(), alertHistoryDaoProvider.get());
  }

  public static EmailAlertService_Factory create(Provider<Context> contextProvider,
      Provider<AlertHistoryDao> alertHistoryDaoProvider) {
    return new EmailAlertService_Factory(contextProvider, alertHistoryDaoProvider);
  }

  public static EmailAlertService newInstance(Context context, AlertHistoryDao alertHistoryDao) {
    return new EmailAlertService(context, alertHistoryDao);
  }
}
