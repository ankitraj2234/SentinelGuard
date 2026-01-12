package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.network.NetworkInfoManager;
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
public final class NetworkInfoViewModel_Factory implements Factory<NetworkInfoViewModel> {
  private final Provider<NetworkInfoManager> networkInfoManagerProvider;

  public NetworkInfoViewModel_Factory(Provider<NetworkInfoManager> networkInfoManagerProvider) {
    this.networkInfoManagerProvider = networkInfoManagerProvider;
  }

  @Override
  public NetworkInfoViewModel get() {
    return newInstance(networkInfoManagerProvider.get());
  }

  public static NetworkInfoViewModel_Factory create(
      Provider<NetworkInfoManager> networkInfoManagerProvider) {
    return new NetworkInfoViewModel_Factory(networkInfoManagerProvider);
  }

  public static NetworkInfoViewModel newInstance(NetworkInfoManager networkInfoManager) {
    return new NetworkInfoViewModel(networkInfoManager);
  }
}
