package com.sentinelguard.di;

import android.content.Context;
import com.sentinelguard.data.database.DatabasePassphraseManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideDatabasePassphraseManagerFactory implements Factory<DatabasePassphraseManager> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideDatabasePassphraseManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DatabasePassphraseManager get() {
    return provideDatabasePassphraseManager(contextProvider.get());
  }

  public static DatabaseModule_ProvideDatabasePassphraseManagerFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideDatabasePassphraseManagerFactory(contextProvider);
  }

  public static DatabasePassphraseManager provideDatabasePassphraseManager(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDatabasePassphraseManager(context));
  }
}
