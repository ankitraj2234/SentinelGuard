package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.BehavioralBaselineDao;
import com.sentinelguard.domain.repository.BaselineRepository;
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
public final class DatabaseModule_ProvideBaselineRepositoryFactory implements Factory<BaselineRepository> {
  private final Provider<BehavioralBaselineDao> daoProvider;

  public DatabaseModule_ProvideBaselineRepositoryFactory(
      Provider<BehavioralBaselineDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public BaselineRepository get() {
    return provideBaselineRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideBaselineRepositoryFactory create(
      Provider<BehavioralBaselineDao> daoProvider) {
    return new DatabaseModule_ProvideBaselineRepositoryFactory(daoProvider);
  }

  public static BaselineRepository provideBaselineRepository(BehavioralBaselineDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBaselineRepository(dao));
  }
}
