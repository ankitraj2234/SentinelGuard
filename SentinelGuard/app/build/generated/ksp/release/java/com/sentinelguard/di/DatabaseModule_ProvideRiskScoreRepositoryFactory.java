package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.RiskScoreDao;
import com.sentinelguard.domain.repository.RiskScoreRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DatabaseModule_ProvideRiskScoreRepositoryFactory implements Factory<RiskScoreRepository> {
  private final Provider<RiskScoreDao> daoProvider;

  public DatabaseModule_ProvideRiskScoreRepositoryFactory(Provider<RiskScoreDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RiskScoreRepository get() {
    return provideRiskScoreRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideRiskScoreRepositoryFactory create(
      Provider<RiskScoreDao> daoProvider) {
    return new DatabaseModule_ProvideRiskScoreRepositoryFactory(daoProvider);
  }

  public static RiskScoreRepository provideRiskScoreRepository(RiskScoreDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRiskScoreRepository(dao));
  }
}
