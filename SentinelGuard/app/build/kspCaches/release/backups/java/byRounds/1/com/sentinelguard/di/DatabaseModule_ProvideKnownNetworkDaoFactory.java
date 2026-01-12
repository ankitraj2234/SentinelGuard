package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.KnownNetworkDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideKnownNetworkDaoFactory implements Factory<KnownNetworkDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideKnownNetworkDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public KnownNetworkDao get() {
    return provideKnownNetworkDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideKnownNetworkDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideKnownNetworkDaoFactory(databaseProvider);
  }

  public static KnownNetworkDao provideKnownNetworkDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideKnownNetworkDao(database));
  }
}
