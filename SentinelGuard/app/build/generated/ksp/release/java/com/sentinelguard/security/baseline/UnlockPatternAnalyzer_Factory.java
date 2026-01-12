package com.sentinelguard.security.baseline;

import android.content.Context;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.data.database.dao.UnlockPatternDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UnlockPatternAnalyzer_Factory implements Factory<UnlockPatternAnalyzer> {
  private final Provider<Context> contextProvider;

  private final Provider<UnlockPatternDao> unlockPatternDaoProvider;

  private final Provider<BehavioralAnomalyDao> anomalyDaoProvider;

  public UnlockPatternAnalyzer_Factory(Provider<Context> contextProvider,
      Provider<UnlockPatternDao> unlockPatternDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    this.contextProvider = contextProvider;
    this.unlockPatternDaoProvider = unlockPatternDaoProvider;
    this.anomalyDaoProvider = anomalyDaoProvider;
  }

  @Override
  public UnlockPatternAnalyzer get() {
    return newInstance(contextProvider.get(), unlockPatternDaoProvider.get(), anomalyDaoProvider.get());
  }

  public static UnlockPatternAnalyzer_Factory create(Provider<Context> contextProvider,
      Provider<UnlockPatternDao> unlockPatternDaoProvider,
      Provider<BehavioralAnomalyDao> anomalyDaoProvider) {
    return new UnlockPatternAnalyzer_Factory(contextProvider, unlockPatternDaoProvider, anomalyDaoProvider);
  }

  public static UnlockPatternAnalyzer newInstance(Context context,
      UnlockPatternDao unlockPatternDao, BehavioralAnomalyDao anomalyDao) {
    return new UnlockPatternAnalyzer(context, unlockPatternDao, anomalyDao);
  }
}
