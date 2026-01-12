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
public final class NetworkSecurityScanner_Factory implements Factory<NetworkSecurityScanner> {
  private final Provider<Context> contextProvider;

  public NetworkSecurityScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkSecurityScanner get() {
    return newInstance(contextProvider.get());
  }

  public static NetworkSecurityScanner_Factory create(Provider<Context> contextProvider) {
    return new NetworkSecurityScanner_Factory(contextProvider);
  }

  public static NetworkSecurityScanner newInstance(Context context) {
    return new NetworkSecurityScanner(context);
  }
}
