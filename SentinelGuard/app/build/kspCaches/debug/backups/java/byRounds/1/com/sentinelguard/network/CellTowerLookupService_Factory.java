package com.sentinelguard.network;

import android.content.Context;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.security.ratelimit.RateLimiter;
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
public final class CellTowerLookupService_Factory implements Factory<CellTowerLookupService> {
  private final Provider<Context> contextProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<RateLimiter> rateLimiterProvider;

  public CellTowerLookupService_Factory(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<RateLimiter> rateLimiterProvider) {
    this.contextProvider = contextProvider;
    this.securePreferencesProvider = securePreferencesProvider;
    this.rateLimiterProvider = rateLimiterProvider;
  }

  @Override
  public CellTowerLookupService get() {
    return newInstance(contextProvider.get(), securePreferencesProvider.get(), rateLimiterProvider.get());
  }

  public static CellTowerLookupService_Factory create(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<RateLimiter> rateLimiterProvider) {
    return new CellTowerLookupService_Factory(contextProvider, securePreferencesProvider, rateLimiterProvider);
  }

  public static CellTowerLookupService newInstance(Context context,
      SecurePreferences securePreferences, RateLimiter rateLimiter) {
    return new CellTowerLookupService(context, securePreferences, rateLimiter);
  }
}
