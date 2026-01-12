package com.sentinelguard.security;

import android.content.Context;
import com.sentinelguard.alert.EmailAlertService;
import com.sentinelguard.data.database.dao.CellTowerDao;
import com.sentinelguard.network.CellTowerLookupService;
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
public final class CellTowerSecurityMonitor_Factory implements Factory<CellTowerSecurityMonitor> {
  private final Provider<Context> contextProvider;

  private final Provider<CellTowerLookupService> cellTowerLookupServiceProvider;

  private final Provider<CellTowerDao> cellTowerDaoProvider;

  private final Provider<EmailAlertService> emailAlertServiceProvider;

  public CellTowerSecurityMonitor_Factory(Provider<Context> contextProvider,
      Provider<CellTowerLookupService> cellTowerLookupServiceProvider,
      Provider<CellTowerDao> cellTowerDaoProvider,
      Provider<EmailAlertService> emailAlertServiceProvider) {
    this.contextProvider = contextProvider;
    this.cellTowerLookupServiceProvider = cellTowerLookupServiceProvider;
    this.cellTowerDaoProvider = cellTowerDaoProvider;
    this.emailAlertServiceProvider = emailAlertServiceProvider;
  }

  @Override
  public CellTowerSecurityMonitor get() {
    return newInstance(contextProvider.get(), cellTowerLookupServiceProvider.get(), cellTowerDaoProvider.get(), emailAlertServiceProvider.get());
  }

  public static CellTowerSecurityMonitor_Factory create(Provider<Context> contextProvider,
      Provider<CellTowerLookupService> cellTowerLookupServiceProvider,
      Provider<CellTowerDao> cellTowerDaoProvider,
      Provider<EmailAlertService> emailAlertServiceProvider) {
    return new CellTowerSecurityMonitor_Factory(contextProvider, cellTowerLookupServiceProvider, cellTowerDaoProvider, emailAlertServiceProvider);
  }

  public static CellTowerSecurityMonitor newInstance(Context context,
      CellTowerLookupService cellTowerLookupService, CellTowerDao cellTowerDao,
      EmailAlertService emailAlertService) {
    return new CellTowerSecurityMonitor(context, cellTowerLookupService, cellTowerDao, emailAlertService);
  }
}
