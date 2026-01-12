package com.sentinelguard.auth;

import com.sentinelguard.data.database.dao.UserDao;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<UserDao> userDaoProvider;

  private final Provider<SecurePreferencesManager> securePrefsProvider;

  public AuthRepository_Factory(Provider<UserDao> userDaoProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    this.userDaoProvider = userDaoProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(userDaoProvider.get(), securePrefsProvider.get());
  }

  public static AuthRepository_Factory create(Provider<UserDao> userDaoProvider,
      Provider<SecurePreferencesManager> securePrefsProvider) {
    return new AuthRepository_Factory(userDaoProvider, securePrefsProvider);
  }

  public static AuthRepository newInstance(UserDao userDao, SecurePreferencesManager securePrefs) {
    return new AuthRepository(userDao, securePrefs);
  }
}
