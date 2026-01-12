package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.IncidentDao;
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
public final class DatabaseModule_ProvideIncidentDaoFactory implements Factory<IncidentDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideIncidentDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IncidentDao get() {
    return provideIncidentDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideIncidentDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideIncidentDaoFactory(databaseProvider);
  }

  public static IncidentDao provideIncidentDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIncidentDao(database));
  }
}
