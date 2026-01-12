package com.sentinelguard.di;

import com.sentinelguard.auth.AuthRepository;
import com.sentinelguard.data.database.dao.UserDao;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
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
public final class AuthModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<UserDao> userDaoProvider;

  private final Provider<SecurePreferencesManager> securePreferencesManagerProvider;

  public AuthModule_ProvideAuthRepositoryFactory(Provider<UserDao> userDaoProvider,
      Provider<SecurePreferencesManager> securePreferencesManagerProvider) {
    this.userDaoProvider = userDaoProvider;
    this.securePreferencesManagerProvider = securePreferencesManagerProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(userDaoProvider.get(), securePreferencesManagerProvider.get());
  }

  public static AuthModule_ProvideAuthRepositoryFactory create(Provider<UserDao> userDaoProvider,
      Provider<SecurePreferencesManager> securePreferencesManagerProvider) {
    return new AuthModule_ProvideAuthRepositoryFactory(userDaoProvider, securePreferencesManagerProvider);
  }

  public static AuthRepository provideAuthRepository(UserDao userDao,
      SecurePreferencesManager securePreferencesManager) {
    return Preconditions.checkNotNullFromProvides(AuthModule.INSTANCE.provideAuthRepository(userDao, securePreferencesManager));
  }
}
