package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.SecuritySignalDao;
import com.sentinelguard.domain.repository.SecuritySignalRepository;
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
public final class DatabaseModule_ProvideSecuritySignalRepositoryFactory implements Factory<SecuritySignalRepository> {
  private final Provider<SecuritySignalDao> daoProvider;

  public DatabaseModule_ProvideSecuritySignalRepositoryFactory(
      Provider<SecuritySignalDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SecuritySignalRepository get() {
    return provideSecuritySignalRepository(daoProvider.get());
  }

  public static DatabaseModule_ProvideSecuritySignalRepositoryFactory create(
      Provider<SecuritySignalDao> daoProvider) {
    return new DatabaseModule_ProvideSecuritySignalRepositoryFactory(daoProvider);
  }

  public static SecuritySignalRepository provideSecuritySignalRepository(SecuritySignalDao dao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSecuritySignalRepository(dao));
  }
}
