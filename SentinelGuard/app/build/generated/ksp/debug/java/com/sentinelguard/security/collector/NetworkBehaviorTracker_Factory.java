package com.sentinelguard.security.collector;

import android.content.Context;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.data.database.dao.KnownNetworkDao;
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
public final class NetworkBehaviorTracker_Factory implements Factory<NetworkBehaviorTracker> {
  private final Provider<Context> contextProvider;

  private final Provider<KnownNetworkDao> knownNetworkDaoProvider;

  private final Provider<BehavioralAnomalyDao> anomalyDaoProvider;

  public NetworkBehaviorTracker_Factory(Provider<Context> contextProvider,
      Provider<KnownNetworkDao> knownNetworkDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    this.contextProvider = contextProvider;
    this.knownNetworkDaoProvider = knownNetworkDaoProvider;
    this.anomalyDaoProvider = anomalyDaoProvider;
  }

  @Override
  public NetworkBehaviorTracker get() {
    return newInstance(contextProvider.get(), knownNetworkDaoProvider.get(), anomalyDaoProvider.get());
  }

  public static NetworkBehaviorTracker_Factory create(Provider<Context> contextProvider,
      Provider<KnownNetworkDao> knownNetworkDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    return new NetworkBehaviorTracker_Factory(contextProvider, knownNetworkDaoProvider, anomalyDaoProvider);
  }

  public static NetworkBehaviorTracker newInstance(Context context, KnownNetworkDao knownNetworkDao,
      BehavioralAnomalyDao anomalyDao) {
    return new NetworkBehaviorTracker(context, knownNetworkDao, anomalyDao);
  }
}
