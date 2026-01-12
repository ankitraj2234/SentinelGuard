package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.AlertQueueDao;
import com.sentinelguard.domain.repository.AlertQueueRepository;
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
public final class DatabaseModule_ProvideAlertQueueRepositoryFactory implements Factory<AlertQueueRepository> {
  private final Provider<AlertQueueDao> daoProvider;

  public DatabaseModule_ProvideAlertQueueRepositoryFactory(Provider<AlertQueueDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AlertQueueRepository get() {
    return provideAlertQueueRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideAlertQueueRepositoryFactory create(
      Provider<AlertQueueDao> daoProvider) {
    return new DatabaseModule_ProvideAlertQueueRepositoryFactory(daoProvider);
  }

  public static AlertQueueRepository provideAlertQueueRepository(AlertQueueDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAlertQueueRepository(dao));
  }
}
