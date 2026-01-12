package com.sentinelguard.ui.viewmodels;

import android.content.Context;
import com.sentinelguard.permission.PermissionManager;
import com.sentinelguard.security.collector.AppUsageTracker;
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
public final class UsageStatisticsViewModel_Factory implements Factory<UsageStatisticsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<AppUsageTracker> appUsageTrackerProvider;

  private final Provider<PermissionManager> permissionManagerProvider;

  public UsageStatisticsViewModel_Factory(Provider<Context> contextProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<PermissionManager> permissionManagerProvider) {
    this.contextProvider = contextProvider;
    this.appUsageTrackerProvider = appUsageTrackerProvider;
    this.permissionManagerProvider = permissionManagerProvider;
  }

  @Override
  public UsageStatisticsViewModel get() {
    return newInstance(contextProvider.get(), appUsageTrackerProvider.get(), permissionManagerProvider.get());
  }

  public static UsageStatisticsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<AppUsageTracker> appUsageTrackerProvider,
      Provider<PermissionManager> permissionManagerProvider) {
    return new UsageStatisticsViewModel_Factory(contextProvider, appUsageTrackerProvider, permissionManagerProvider);
  }

  public static UsageStatisticsViewModel newInstance(Context context,
      AppUsageTracker appUsageTracker, PermissionManager permissionManager) {
    return new UsageStatisticsViewModel(context, appUsageTracker, permissionManager);
  }
}
