package com.sentinelguard.ui.screens;

import com.sentinelguard.permission.PermissionManager;
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
public final class PermissionViewModel_Factory implements Factory<PermissionViewModel> {
  private final Provider<PermissionManager> permissionManagerProvider;

  public PermissionViewModel_Factory(Provider<PermissionManager> permissionManagerProvider) {
    this.permissionManagerProvider = permissionManagerProvider;
  }

  @Override
  public PermissionViewModel get() {
    return newInstance(permissionManagerProvider.get());
  }

  public static PermissionViewModel_Factory create(
      Provider<PermissionManager> permissionManagerProvider) {
    return new PermissionViewModel_Factory(permissionManagerProvider);
  }

  public static PermissionViewModel newInstance(PermissionManager permissionManager) {
    return new PermissionViewModel(permissionManager);
  }
}
