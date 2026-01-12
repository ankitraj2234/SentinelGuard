package com.sentinelguard.di;

import com.sentinelguard.data.database.dao.UserDao;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.domain.repository.UserRepository;
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
public final class DatabaseModule_ProvideUserRepositoryFactory implements Factory<UserRepository> {
  private final Provider<UserDao> userDaoProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public DatabaseModule_ProvideUserRepositoryFactory(Provider<UserDao> userDaoProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.userDaoProvider = userDaoProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public UserRepository get() {
    return provideUserRepository(userDaoProvider.get(), securePreferencesProvider.get());
  }

  public static DatabaseModule_ProvideUserRepositoryFactory create(
      Provider<UserDao> userDaoProvider, Provider<SecurePreferences> securePreferencesProvider) {
    return new DatabaseModule_ProvideUserRepositoryFactory(userDaoProvider, securePreferencesProvider);
  }

  public static UserRepository provideUserRepository(UserDao userDao,
      SecurePreferences securePreferences) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUserRepository(userDao, securePreferences));
  }
}
