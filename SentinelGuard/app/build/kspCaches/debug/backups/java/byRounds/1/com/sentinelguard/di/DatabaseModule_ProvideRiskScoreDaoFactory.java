package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.RiskScoreDao;
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
public final class DatabaseModule_ProvideRiskScoreDaoFactory implements Factory<RiskScoreDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideRiskScoreDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RiskScoreDao get() {
    return provideRiskScoreDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideRiskScoreDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRiskScoreDaoFactory(databaseProvider);
  }

  public static RiskScoreDao provideRiskScoreDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRiskScoreDao(database));
  }
}
