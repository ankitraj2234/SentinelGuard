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
public final class SystemIntegrityScanner_Factory implements Factory<SystemIntegrityScanner> {
  private final Provider<Context> contextProvider;

  public SystemIntegrityScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SystemIntegrityScanner get() {
    return newInstance(contextProvider.get());
  }

  public static SystemIntegrityScanner_Factory create(Provider<Context> contextProvider) {
    return new SystemIntegrityScanner_Factory(contextProvider);
  }

  public static SystemIntegrityScanner newInstance(Context context) {
    return new SystemIntegrityScanner(context);
  }
}
