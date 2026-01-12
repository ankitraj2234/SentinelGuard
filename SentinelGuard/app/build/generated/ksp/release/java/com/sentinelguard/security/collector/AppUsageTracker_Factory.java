package com.sentinelguard.security.collector;

import android.content.Context;
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
public final class AppUsageTracker_Factory implements Factory<AppUsageTracker> {
  private final Provider<Context> contextProvider;

  private final Provider<PermissionManager> permissionManagerProvider;

  public AppUsageTracker_Factory(Provider<Context> contextProvider,
      Provider<PermissionManager> permissionManagerProvider) {
    this.contextProvider = contextProvider;
    this.permissionManagerProvider = permissionManagerProvider;
  }

  @Override
  public AppUsageTracker get() {
    return newInstance(contextProvider.get(), permissionManagerProvider.get());
  }

  public static AppUsageTracker_Factory create(Provider<Context> contextProvider,
      Provider<PermissionManager> permissionManagerProvider) {
    return new AppUsageTracker_Factory(contextProvider, permissionManagerProvider);
  }

  public static AppUsageTracker newInstance(Context context, PermissionManager permissionManager) {
    return new AppUsageTracker(context, permissionManager);
  }
}
