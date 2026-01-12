package com.sentinelguard.di;

import android.content.Context;
import com.sentinelguard.data.database.AppDatabase;
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
public final class DatabaseModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  private final Provider<DatabasePassphraseManager> passphraseManagerProvider;

  public DatabaseModule_ProvideAppDatabaseFactory(Provider<Context> contextProvider,
      Provider<DatabasePassphraseManager> passphraseManagerProvider) {
    this.contextProvider = contextProvider;
    this.passphraseManagerProvider = passphraseManagerProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(contextProvider.get(), passphraseManagerProvider.get());
  }

  public static DatabaseModule_ProvideAppDatabaseFactory create(Provider<Context> contextProvider,
      Provider<DatabasePassphraseManager> passphraseManagerProvider) {
    return new DatabaseModule_ProvideAppDatabaseFactory(contextProvider, passphraseManagerProvider);
  }

  public static AppDatabase provideAppDatabase(Context context,
      DatabasePassphraseManager passphraseManager) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAppDatabase(context, passphraseManager));
  }
}
