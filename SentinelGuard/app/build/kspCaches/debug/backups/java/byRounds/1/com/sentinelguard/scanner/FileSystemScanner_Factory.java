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
public final class FileSystemScanner_Factory implements Factory<FileSystemScanner> {
  private final Provider<Context> contextProvider;

  public FileSystemScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FileSystemScanner get() {
    return newInstance(contextProvider.get());
  }

  public static FileSystemScanner_Factory create(Provider<Context> contextProvider) {
    return new FileSystemScanner_Factory(contextProvider);
  }

  public static FileSystemScanner newInstance(Context context) {
    return new FileSystemScanner(context);
  }
}
