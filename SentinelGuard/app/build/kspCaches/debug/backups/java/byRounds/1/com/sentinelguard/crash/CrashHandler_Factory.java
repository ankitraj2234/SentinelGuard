package com.sentinelguard.crash;

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
public final class CrashHandler_Factory implements Factory<CrashHandler> {
  private final Provider<Context> contextProvider;

  public CrashHandler_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CrashHandler get() {
    return newInstance(contextProvider.get());
  }

  public static CrashHandler_Factory create(Provider<Context> contextProvider) {
    return new CrashHandler_Factory(contextProvider);
  }

  public static CrashHandler newInstance(Context context) {
    return new CrashHandler(context);
  }
}
