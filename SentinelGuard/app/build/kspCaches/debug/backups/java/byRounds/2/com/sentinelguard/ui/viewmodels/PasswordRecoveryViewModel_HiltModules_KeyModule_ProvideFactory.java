package com.sentinelguard.ui.viewmodels;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.internal.lifecycle.HiltViewModelMap.KeySet")
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
public final class PasswordRecoveryViewModel_HiltModules_KeyModule_ProvideFactory implements Factory<Boolean> {
  @Override
  public Boolean get() {
    return provide();
  }

  public static PasswordRecoveryViewModel_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static boolean provide() {
    return PasswordRecoveryViewModel_HiltModules.KeyModule.provide();
  }

  private static final class InstanceHolder {
    private static final PasswordRecoveryViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new PasswordRecoveryViewModel_HiltModules_KeyModule_ProvideFactory();
  }
}
