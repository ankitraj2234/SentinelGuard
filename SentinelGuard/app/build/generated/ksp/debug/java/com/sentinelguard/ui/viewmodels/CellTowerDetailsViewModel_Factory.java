package com.sentinelguard.ui.viewmodels;

import android.content.Context;
import com.sentinelguard.data.database.dao.CellTowerDao;
import com.sentinelguard.network.CellTowerLookupService;
import com.sentinelguard.security.CellTowerSecurityMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CellTowerDetailsViewModel_Factory implements Factory<CellTowerDetailsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<CellTowerLookupService> cellTowerLookupServiceProvider;

  private final Provider<CellTowerSecurityMonitor> cellTowerSecurityMonitorProvider;

  private final Provider<CellTowerDao> cellTowerDaoProvider;

  public CellTowerDetailsViewModel_Factory(Provider<Context> contextProvider,
      Provider<CellTowerLookupService> cellTowerLookupServiceProvider,
      Provider<CellTowerSecurityMonitor> cellTowerSecurityMonitorProvider,
      Provider<CellTowerDao> cellTowerDaoProvider) {
    this.contextProvider = contextProvider;
    this.cellTowerLookupServiceProvider = cellTowerLookupServiceProvider;
    this.cellTowerSecurityMonitorProvider = cellTowerSecurityMonitorProvider;
    this.cellTowerDaoProvider = cellTowerDaoProvider;
  }

  @Override
  public CellTowerDetailsViewModel get() {
    return newInstance(contextProvider.get(), cellTowerLookupServiceProvider.get(), cellTowerSecurityMonitorProvider.get(), cellTowerDaoProvider.get());
  }

  public static CellTowerDetailsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<CellTowerLookupService> cellTowerLookupServiceProvider,
      Provider<CellTowerSecurityMonitor> cellTowerSecurityMonitorProvider,
      Provider<CellTowerDao> cellTowerDaoProvider) {
    return new CellTowerDetailsViewModel_Factory(contextProvider, cellTowerLookupServiceProvider, cellTowerSecurityMonitorProvider, cellTowerDaoProvider);
  }

  public static CellTowerDetailsViewModel newInstance(Context context,
      CellTowerLookupService cellTowerLookupService,
      CellTowerSecurityMonitor cellTowerSecurityMonitor, CellTowerDao cellTowerDao) {
    return new CellTowerDetailsViewModel(context, cellTowerLookupService, cellTowerSecurityMonitor, cellTowerDao);
  }
}
