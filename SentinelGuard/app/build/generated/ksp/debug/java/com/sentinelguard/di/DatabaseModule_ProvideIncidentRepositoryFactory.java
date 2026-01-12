package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.IncidentDao;
import com.sentinelguard.domain.repository.IncidentRepository;
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
public final class DatabaseModule_ProvideIncidentRepositoryFactory implements Factory<IncidentRepository> {
  private final Provider<IncidentDao> daoProvider;

  public DatabaseModule_ProvideIncidentRepositoryFactory(Provider<IncidentDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public IncidentRepository get() {
    return provideIncidentRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideIncidentRepositoryFactory create(
      Provider<IncidentDao> daoProvider) {
    return new DatabaseModule_ProvideIncidentRepositoryFactory(daoProvider);
  }

  public static IncidentRepository provideIncidentRepository(IncidentDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIncidentRepository(dao));
  }
}
