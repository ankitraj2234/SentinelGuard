package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
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
public final class DatabaseModule_ProvideBehavioralAnomalyDaoFactory implements Factory<BehavioralAnomalyDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideBehavioralAnomalyDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BehavioralAnomalyDao get() {
    return provideBehavioralAnomalyDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideBehavioralAnomalyDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBehavioralAnomalyDaoFactory(databaseProvider);
  }

  public static BehavioralAnomalyDao provideBehavioralAnomalyDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBehavioralAnomalyDao(database));
  }
}
