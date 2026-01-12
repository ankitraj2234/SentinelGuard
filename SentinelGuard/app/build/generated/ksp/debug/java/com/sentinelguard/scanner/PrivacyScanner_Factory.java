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
public final class PrivacyScanner_Factory implements Factory<PrivacyScanner> {
  private final Provider<Context> contextProvider;

  public PrivacyScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PrivacyScanner get() {
    return newInstance(contextProvider.get());
  }

  public static PrivacyScanner_Factory create(Provider<Context> contextProvider) {
    return new PrivacyScanner_Factory(contextProvider);
  }

  public static PrivacyScanner newInstance(Context context) {
    return new PrivacyScanner(context);
  }
}
