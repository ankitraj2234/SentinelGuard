package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.BehavioralBaselineDao;
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
public final class DatabaseModule_ProvideBehavioralBaselineDaoFactory implements Factory<BehavioralBaselineDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideBehavioralBaselineDaoFactory(
      Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BehavioralBaselineDao get() {
    return provideBehavioralBaselineDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideBehavioralBaselineDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBehavioralBaselineDaoFactory(databaseProvider);
  }

  public static BehavioralBaselineDao provideBehavioralBaselineDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBehavioralBaselineDao(database));
  }
}
