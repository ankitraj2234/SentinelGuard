package com.sentinelguard.di;

import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.dao.UnlockPatternDao;
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
public final class DatabaseModule_ProvideUnlockPatternDaoFactory implements Factory<UnlockPatternDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideUnlockPatternDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public UnlockPatternDao get() {
    return provideUnlockPatternDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideUnlockPatternDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideUnlockPatternDaoFactory(databaseProvider);
  }

  public static UnlockPatternDao provideUnlockPatternDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUnlockPatternDao(database));
  }
}
