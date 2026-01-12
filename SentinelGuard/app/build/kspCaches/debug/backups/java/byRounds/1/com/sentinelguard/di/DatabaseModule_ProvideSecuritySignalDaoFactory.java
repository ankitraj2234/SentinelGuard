package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.SecuritySignalDao;
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
public final class DatabaseModule_ProvideSecuritySignalDaoFactory implements Factory<SecuritySignalDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSecuritySignalDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SecuritySignalDao get() {
    return provideSecuritySignalDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSecuritySignalDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSecuritySignalDaoFactory(databaseProvider);
  }

  public static SecuritySignalDao provideSecuritySignalDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSecuritySignalDao(database));
  }
}
