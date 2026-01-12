package com.sentinelguard.ui.viewmodels;

import com.sentinelguard.incident.TimelineBuilder;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TimelineViewModel_Factory implements Factory<TimelineViewModel> {
  private final Provider<TimelineBuilder> timelineBuilderProvider;

  public TimelineViewModel_Factory(Provider<TimelineBuilder> timelineBuilderProvider) {
    this.timelineBuilderProvider = timelineBuilderProvider;
  }

  @Override
  public TimelineViewModel get() {
    return newInstance(timelineBuilderProvider.get());
  }

  public static TimelineViewModel_Factory create(
      Provider<TimelineBuilder> timelineBuilderProvider) {
    return new TimelineViewModel_Factory(timelineBuilderProvider);
  }

  public static TimelineViewModel newInstance(TimelineBuilder timelineBuilder) {
    return new TimelineViewModel(timelineBuilder);
  }
}
