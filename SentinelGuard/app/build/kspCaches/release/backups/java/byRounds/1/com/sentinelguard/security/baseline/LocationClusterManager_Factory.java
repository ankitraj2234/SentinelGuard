package com.sentinelguard.security.baseline;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.data.database.dao.LocationClusterDao;
import com.sentinelguard.permission.PermissionManager;
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
public final class LocationClusterManager_Factory implements Factory<LocationClusterManager> {
  private final Provider<Context> contextProvider;

  private final Provider<LocationClusterDao> locationClusterDaoProvider;

  private final Provider<BehavioralAnomalyDao> anomalyDaoProvider;

  private final Provider<PermissionManager> permissionManagerProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public LocationClusterManager_Factory(Provider<Context> contextProvider,
      Provider<LocationClusterDao> locationClusterDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider,
      Provider<PermissionManager> permissionManagerProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.contextProvider = contextProvider;
    this.locationClusterDaoProvider = locationClusterDaoProvider;
    this.anomalyDaoProvider = anomalyDaoProvider;
    this.permissionManagerProvider = permissionManagerProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  @Override
  public LocationClusterManager get() {
    return newInstance(contextProvider.get(), locationClusterDaoProvider.get(), anomalyDaoProvider.get(), permissionManagerProvider.get(), fusedLocationClientProvider.get());
  }

  public static LocationClusterManager_Factory create(Provider<Context> contextProvider,
      Provider<LocationClusterDao> locationClusterDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider,
      Provider<PermissionManager> permissionManagerProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new LocationClusterManager_Factory(contextProvider, locationClusterDaoProvider, anomalyDaoProvider, permissionManagerProvider, fusedLocationClientProvider);
  }

  public static LocationClusterManager newInstance(Context context,
      LocationClusterDao locationClusterDao, BehavioralAnomalyDao anomalyDao,
      PermissionManager permissionManager, FusedLocationProviderClient fusedLocationClient) {
    return new LocationClusterManager(context, locationClusterDao, anomalyDao, permissionManager, fusedLocationClient);
  }
}
