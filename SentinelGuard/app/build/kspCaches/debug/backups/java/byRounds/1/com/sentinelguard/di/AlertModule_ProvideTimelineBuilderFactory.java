package com.sentinelguard.di;

import com.sentinelguard.domain.repository.IncidentRepository;
import com.sentinelguard.domain.repository.SecuritySignalRepository;
import com.sentinelguard.incident.TimelineBuilder;
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
public final class AlertModule_ProvideTimelineBuilderFactory implements Factory<TimelineBuilder> {
  private final Provider<SecuritySignalRepository> signalRepositoryProvider;

  private final Provider<IncidentRepository> incidentRepositoryProvider;

  public AlertModule_ProvideTimelineBuilderFactory(
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<IncidentRepository> incidentRepositoryProvider) {
    this.signalRepositoryProvider = signalRepositoryProvider;
    this.incidentRepositoryProvider = incidentRepositoryProvider;
  }

  @Override
  public TimelineBuilder get() {
    return provideTimelineBuilder(signalRepositoryProvider.get(), incidentRepositoryProvider.get());
  }

  public static AlertModule_ProvideTimelineBuilderFactory create(
      Provider<SecuritySignalRepository> signalRepositoryProvider,
      Provider<IncidentRepository> incidentRepositoryProvider) {
    return new AlertModule_ProvideTimelineBuilderFactory(signalRepositoryProvider, incidentRepositoryProvider);
  }

  public static TimelineBuilder provideTimelineBuilder(SecuritySignalRepository signalRepository,
      IncidentRepository incidentRepository) {
    return Preconditions.checkNotNullFromProvides(AlertModule.INSTANCE.provideTimelineBuilder(signalRepository, incidentRepository));
  }
}
