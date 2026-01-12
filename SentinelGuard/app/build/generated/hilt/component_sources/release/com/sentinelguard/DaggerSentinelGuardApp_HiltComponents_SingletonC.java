package com.sentinelguard;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.sentinelguard.auth.AuthRepository;
import com.sentinelguard.auth.BiometricAuthManager;
import com.sentinelguard.data.database.AppDatabase;
import com.sentinelguard.data.database.DatabasePassphraseManager;
import com.sentinelguard.data.database.dao.AppUsagePatternDao;
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao;
import com.sentinelguard.data.database.dao.BehavioralBaselineDao;
import com.sentinelguard.data.database.dao.IncidentDao;
import com.sentinelguard.data.database.dao.KnownNetworkDao;
import com.sentinelguard.data.database.dao.LocationClusterDao;
import com.sentinelguard.data.database.dao.RiskScoreDao;
import com.sentinelguard.data.database.dao.SecuritySignalDao;
import com.sentinelguard.data.database.dao.UnlockPatternDao;
import com.sentinelguard.data.database.dao.UserDao;
import com.sentinelguard.data.local.preferences.SecurePreferences;
import com.sentinelguard.data.preferences.SecurePreferencesManager;
import com.sentinelguard.di.AlertModule_ProvideTimelineBuilderFactory;
import com.sentinelguard.di.AuthModule_ProvideAuthRepositoryFactory;
import com.sentinelguard.di.AuthModule_ProvideBiometricAuthManagerFactory;
import com.sentinelguard.di.AuthModule_ProvideSecurePreferencesManagerFactory;
import com.sentinelguard.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.sentinelguard.di.DatabaseModule_ProvideAppUsagePatternDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideBaselineRepositoryFactory;
import com.sentinelguard.di.DatabaseModule_ProvideBehavioralAnomalyDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideBehavioralBaselineDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideDatabasePassphraseManagerFactory;
import com.sentinelguard.di.DatabaseModule_ProvideIncidentDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideIncidentRepositoryFactory;
import com.sentinelguard.di.DatabaseModule_ProvideKnownNetworkDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideLocationClusterDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideRiskScoreDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideRiskScoreRepositoryFactory;
import com.sentinelguard.di.DatabaseModule_ProvideSecuritySignalDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideSecuritySignalRepositoryFactory;
import com.sentinelguard.di.DatabaseModule_ProvideUnlockPatternDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideUserDaoFactory;
import com.sentinelguard.di.DatabaseModule_ProvideUserRepositoryFactory;
import com.sentinelguard.di.SecurityModule_ProvideBaselineEngineFactory;
import com.sentinelguard.di.SecurityModule_ProvideFusedLocationClientFactory;
import com.sentinelguard.di.SecurityModule_ProvideSecurePreferencesFactory;
import com.sentinelguard.di.SecurityModule_ProvideSignalCollectorFactory;
import com.sentinelguard.domain.repository.BaselineRepository;
import com.sentinelguard.domain.repository.IncidentRepository;
import com.sentinelguard.domain.repository.RiskScoreRepository;
import com.sentinelguard.domain.repository.SecuritySignalRepository;
import com.sentinelguard.domain.repository.UserRepository;
import com.sentinelguard.email.EmailCredentialInitializer;
import com.sentinelguard.email.EmailService;
import com.sentinelguard.incident.TimelineBuilder;
import com.sentinelguard.permission.PermissionManager;
import com.sentinelguard.scanner.MalwareScanner;
import com.sentinelguard.security.alert.SecurityAlertManager;
import com.sentinelguard.security.baseline.AppUsagePatternAnalyzer;
import com.sentinelguard.security.baseline.BaselineEngine;
import com.sentinelguard.security.baseline.LocationClusterManager;
import com.sentinelguard.security.baseline.UnlockPatternAnalyzer;
import com.sentinelguard.security.collector.AppUsageTracker;
import com.sentinelguard.security.collector.NetworkBehaviorTracker;
import com.sentinelguard.security.collector.SignalCollector;
import com.sentinelguard.security.risk.RiskScoringEngine;
import com.sentinelguard.service.MonitoringService;
import com.sentinelguard.service.MonitoringService_MembersInjector;
import com.sentinelguard.ui.MainActivity;
import com.sentinelguard.ui.screens.PermissionViewModel;
import com.sentinelguard.ui.screens.PermissionViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.DashboardViewModel;
import com.sentinelguard.ui.viewmodels.DashboardViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.LoginViewModel;
import com.sentinelguard.ui.viewmodels.LoginViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.MainViewModel;
import com.sentinelguard.ui.viewmodels.MainViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel;
import com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.ScanViewModel;
import com.sentinelguard.ui.viewmodels.ScanViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.SettingsViewModel;
import com.sentinelguard.ui.viewmodels.SettingsViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.SetupViewModel;
import com.sentinelguard.ui.viewmodels.SetupViewModel_HiltModules;
import com.sentinelguard.ui.viewmodels.TimelineViewModel;
import com.sentinelguard.ui.viewmodels.TimelineViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerSentinelGuardApp_HiltComponents_SingletonC {
  private DaggerSentinelGuardApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SentinelGuardApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SentinelGuardApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SentinelGuardApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SentinelGuardApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SentinelGuardApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SentinelGuardApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SentinelGuardApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SentinelGuardApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SentinelGuardApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SentinelGuardApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SentinelGuardApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SentinelGuardApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SentinelGuardApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_LoginViewModel, LoginViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_MainViewModel, MainViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel, PasswordRecoveryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_screens_PermissionViewModel, PermissionViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_ScanViewModel, ScanViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_SetupViewModel, SetupViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_TimelineViewModel, TimelineViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sentinelguard_ui_viewmodels_LoginViewModel = "com.sentinelguard.ui.viewmodels.LoginViewModel";

      static String com_sentinelguard_ui_viewmodels_SettingsViewModel = "com.sentinelguard.ui.viewmodels.SettingsViewModel";

      static String com_sentinelguard_ui_viewmodels_DashboardViewModel = "com.sentinelguard.ui.viewmodels.DashboardViewModel";

      static String com_sentinelguard_ui_viewmodels_MainViewModel = "com.sentinelguard.ui.viewmodels.MainViewModel";

      static String com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel = "com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel";

      static String com_sentinelguard_ui_viewmodels_TimelineViewModel = "com.sentinelguard.ui.viewmodels.TimelineViewModel";

      static String com_sentinelguard_ui_viewmodels_ScanViewModel = "com.sentinelguard.ui.viewmodels.ScanViewModel";

      static String com_sentinelguard_ui_viewmodels_SetupViewModel = "com.sentinelguard.ui.viewmodels.SetupViewModel";

      static String com_sentinelguard_ui_screens_PermissionViewModel = "com.sentinelguard.ui.screens.PermissionViewModel";

      @KeepFieldType
      LoginViewModel com_sentinelguard_ui_viewmodels_LoginViewModel2;

      @KeepFieldType
      SettingsViewModel com_sentinelguard_ui_viewmodels_SettingsViewModel2;

      @KeepFieldType
      DashboardViewModel com_sentinelguard_ui_viewmodels_DashboardViewModel2;

      @KeepFieldType
      MainViewModel com_sentinelguard_ui_viewmodels_MainViewModel2;

      @KeepFieldType
      PasswordRecoveryViewModel com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel2;

      @KeepFieldType
      TimelineViewModel com_sentinelguard_ui_viewmodels_TimelineViewModel2;

      @KeepFieldType
      ScanViewModel com_sentinelguard_ui_viewmodels_ScanViewModel2;

      @KeepFieldType
      SetupViewModel com_sentinelguard_ui_viewmodels_SetupViewModel2;

      @KeepFieldType
      PermissionViewModel com_sentinelguard_ui_screens_PermissionViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SentinelGuardApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private Provider<PasswordRecoveryViewModel> passwordRecoveryViewModelProvider;

    private Provider<PermissionViewModel> permissionViewModelProvider;

    private Provider<ScanViewModel> scanViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SetupViewModel> setupViewModelProvider;

    private Provider<TimelineViewModel> timelineViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.passwordRecoveryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.permissionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.scanViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.setupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.timelineViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_LoginViewModel, ((Provider) loginViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_MainViewModel, ((Provider) mainViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel, ((Provider) passwordRecoveryViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_screens_PermissionViewModel, ((Provider) permissionViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_ScanViewModel, ((Provider) scanViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_SetupViewModel, ((Provider) setupViewModelProvider)).put(LazyClassKeyProvider.com_sentinelguard_ui_viewmodels_TimelineViewModel, ((Provider) timelineViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sentinelguard_ui_viewmodels_DashboardViewModel = "com.sentinelguard.ui.viewmodels.DashboardViewModel";

      static String com_sentinelguard_ui_viewmodels_MainViewModel = "com.sentinelguard.ui.viewmodels.MainViewModel";

      static String com_sentinelguard_ui_viewmodels_SetupViewModel = "com.sentinelguard.ui.viewmodels.SetupViewModel";

      static String com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel = "com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel";

      static String com_sentinelguard_ui_screens_PermissionViewModel = "com.sentinelguard.ui.screens.PermissionViewModel";

      static String com_sentinelguard_ui_viewmodels_LoginViewModel = "com.sentinelguard.ui.viewmodels.LoginViewModel";

      static String com_sentinelguard_ui_viewmodels_SettingsViewModel = "com.sentinelguard.ui.viewmodels.SettingsViewModel";

      static String com_sentinelguard_ui_viewmodels_TimelineViewModel = "com.sentinelguard.ui.viewmodels.TimelineViewModel";

      static String com_sentinelguard_ui_viewmodels_ScanViewModel = "com.sentinelguard.ui.viewmodels.ScanViewModel";

      @KeepFieldType
      DashboardViewModel com_sentinelguard_ui_viewmodels_DashboardViewModel2;

      @KeepFieldType
      MainViewModel com_sentinelguard_ui_viewmodels_MainViewModel2;

      @KeepFieldType
      SetupViewModel com_sentinelguard_ui_viewmodels_SetupViewModel2;

      @KeepFieldType
      PasswordRecoveryViewModel com_sentinelguard_ui_viewmodels_PasswordRecoveryViewModel2;

      @KeepFieldType
      PermissionViewModel com_sentinelguard_ui_screens_PermissionViewModel2;

      @KeepFieldType
      LoginViewModel com_sentinelguard_ui_viewmodels_LoginViewModel2;

      @KeepFieldType
      SettingsViewModel com_sentinelguard_ui_viewmodels_SettingsViewModel2;

      @KeepFieldType
      TimelineViewModel com_sentinelguard_ui_viewmodels_TimelineViewModel2;

      @KeepFieldType
      ScanViewModel com_sentinelguard_ui_viewmodels_ScanViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sentinelguard.ui.viewmodels.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.riskScoringEngineProvider.get(), singletonCImpl.provideBaselineEngineProvider.get(), singletonCImpl.provideSecurePreferencesManagerProvider.get());

          case 1: // com.sentinelguard.ui.viewmodels.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.provideBiometricAuthManagerProvider.get(), singletonCImpl.provideSignalCollectorProvider.get());

          case 2: // com.sentinelguard.ui.viewmodels.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.riskScoringEngineProvider.get(), singletonCImpl.provideBaselineEngineProvider.get(), singletonCImpl.provideSecurePreferencesManagerProvider.get());

          case 3: // com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel 
          return (T) new PasswordRecoveryViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.emailServiceProvider.get());

          case 4: // com.sentinelguard.ui.screens.PermissionViewModel 
          return (T) new PermissionViewModel(singletonCImpl.permissionManagerProvider.get());

          case 5: // com.sentinelguard.ui.viewmodels.ScanViewModel 
          return (T) new ScanViewModel(singletonCImpl.malwareScannerProvider.get(), singletonCImpl.provideSecurePreferencesProvider.get());

          case 6: // com.sentinelguard.ui.viewmodels.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideSecurePreferencesProvider.get(), singletonCImpl.provideSecurePreferencesManagerProvider.get(), singletonCImpl.provideUserRepositoryProvider.get());

          case 7: // com.sentinelguard.ui.viewmodels.SetupViewModel 
          return (T) new SetupViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.provideSecurePreferencesManagerProvider.get());

          case 8: // com.sentinelguard.ui.viewmodels.TimelineViewModel 
          return (T) new TimelineViewModel(singletonCImpl.provideTimelineBuilderProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SentinelGuardApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SentinelGuardApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectMonitoringService(MonitoringService monitoringService) {
      injectMonitoringService2(monitoringService);
    }

    private MonitoringService injectMonitoringService2(MonitoringService instance) {
      MonitoringService_MembersInjector.injectAppUsageTracker(instance, singletonCImpl.appUsageTrackerProvider.get());
      MonitoringService_MembersInjector.injectBaselineEngine(instance, singletonCImpl.provideBaselineEngineProvider.get());
      MonitoringService_MembersInjector.injectRiskScoringEngine(instance, singletonCImpl.riskScoringEngineProvider.get());
      MonitoringService_MembersInjector.injectSecurePreferences(instance, singletonCImpl.provideSecurePreferencesProvider.get());
      MonitoringService_MembersInjector.injectAlertManager(instance, singletonCImpl.securityAlertManagerProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends SentinelGuardApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<EmailService> emailServiceProvider;

    private Provider<EmailCredentialInitializer> emailCredentialInitializerProvider;

    private Provider<DatabasePassphraseManager> provideDatabasePassphraseManagerProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<RiskScoreRepository> provideRiskScoreRepositoryProvider;

    private Provider<SecuritySignalRepository> provideSecuritySignalRepositoryProvider;

    private Provider<BaselineRepository> provideBaselineRepositoryProvider;

    private Provider<SecurePreferences> provideSecurePreferencesProvider;

    private Provider<BaselineEngine> provideBaselineEngineProvider;

    private Provider<PermissionManager> permissionManagerProvider;

    private Provider<AppUsageTracker> appUsageTrackerProvider;

    private Provider<AppUsagePatternAnalyzer> appUsagePatternAnalyzerProvider;

    private Provider<FusedLocationProviderClient> provideFusedLocationClientProvider;

    private Provider<LocationClusterManager> locationClusterManagerProvider;

    private Provider<NetworkBehaviorTracker> networkBehaviorTrackerProvider;

    private Provider<UnlockPatternAnalyzer> unlockPatternAnalyzerProvider;

    private Provider<RiskScoringEngine> riskScoringEngineProvider;

    private Provider<SecurePreferencesManager> provideSecurePreferencesManagerProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private Provider<BiometricAuthManager> provideBiometricAuthManagerProvider;

    private Provider<SignalCollector> provideSignalCollectorProvider;

    private Provider<MalwareScanner> malwareScannerProvider;

    private Provider<UserRepository> provideUserRepositoryProvider;

    private Provider<IncidentRepository> provideIncidentRepositoryProvider;

    private Provider<TimelineBuilder> provideTimelineBuilderProvider;

    private Provider<SecurityAlertManager> securityAlertManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private RiskScoreDao riskScoreDao() {
      return DatabaseModule_ProvideRiskScoreDaoFactory.provideRiskScoreDao(provideAppDatabaseProvider.get());
    }

    private SecuritySignalDao securitySignalDao() {
      return DatabaseModule_ProvideSecuritySignalDaoFactory.provideSecuritySignalDao(provideAppDatabaseProvider.get());
    }

    private BehavioralBaselineDao behavioralBaselineDao() {
      return DatabaseModule_ProvideBehavioralBaselineDaoFactory.provideBehavioralBaselineDao(provideAppDatabaseProvider.get());
    }

    private BehavioralAnomalyDao behavioralAnomalyDao() {
      return DatabaseModule_ProvideBehavioralAnomalyDaoFactory.provideBehavioralAnomalyDao(provideAppDatabaseProvider.get());
    }

    private AppUsagePatternDao appUsagePatternDao() {
      return DatabaseModule_ProvideAppUsagePatternDaoFactory.provideAppUsagePatternDao(provideAppDatabaseProvider.get());
    }

    private LocationClusterDao locationClusterDao() {
      return DatabaseModule_ProvideLocationClusterDaoFactory.provideLocationClusterDao(provideAppDatabaseProvider.get());
    }

    private KnownNetworkDao knownNetworkDao() {
      return DatabaseModule_ProvideKnownNetworkDaoFactory.provideKnownNetworkDao(provideAppDatabaseProvider.get());
    }

    private UnlockPatternDao unlockPatternDao() {
      return DatabaseModule_ProvideUnlockPatternDaoFactory.provideUnlockPatternDao(provideAppDatabaseProvider.get());
    }

    private UserDao userDao() {
      return DatabaseModule_ProvideUserDaoFactory.provideUserDao(provideAppDatabaseProvider.get());
    }

    private IncidentDao incidentDao() {
      return DatabaseModule_ProvideIncidentDaoFactory.provideIncidentDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.emailServiceProvider = DoubleCheck.provider(new SwitchingProvider<EmailService>(singletonCImpl, 1));
      this.emailCredentialInitializerProvider = DoubleCheck.provider(new SwitchingProvider<EmailCredentialInitializer>(singletonCImpl, 0));
      this.provideDatabasePassphraseManagerProvider = DoubleCheck.provider(new SwitchingProvider<DatabasePassphraseManager>(singletonCImpl, 5));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 4));
      this.provideRiskScoreRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RiskScoreRepository>(singletonCImpl, 3));
      this.provideSecuritySignalRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SecuritySignalRepository>(singletonCImpl, 6));
      this.provideBaselineRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BaselineRepository>(singletonCImpl, 8));
      this.provideSecurePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SecurePreferences>(singletonCImpl, 9));
      this.provideBaselineEngineProvider = DoubleCheck.provider(new SwitchingProvider<BaselineEngine>(singletonCImpl, 7));
      this.permissionManagerProvider = DoubleCheck.provider(new SwitchingProvider<PermissionManager>(singletonCImpl, 12));
      this.appUsageTrackerProvider = DoubleCheck.provider(new SwitchingProvider<AppUsageTracker>(singletonCImpl, 11));
      this.appUsagePatternAnalyzerProvider = DoubleCheck.provider(new SwitchingProvider<AppUsagePatternAnalyzer>(singletonCImpl, 10));
      this.provideFusedLocationClientProvider = DoubleCheck.provider(new SwitchingProvider<FusedLocationProviderClient>(singletonCImpl, 14));
      this.locationClusterManagerProvider = DoubleCheck.provider(new SwitchingProvider<LocationClusterManager>(singletonCImpl, 13));
      this.networkBehaviorTrackerProvider = DoubleCheck.provider(new SwitchingProvider<NetworkBehaviorTracker>(singletonCImpl, 15));
      this.unlockPatternAnalyzerProvider = DoubleCheck.provider(new SwitchingProvider<UnlockPatternAnalyzer>(singletonCImpl, 16));
      this.riskScoringEngineProvider = DoubleCheck.provider(new SwitchingProvider<RiskScoringEngine>(singletonCImpl, 2));
      this.provideSecurePreferencesManagerProvider = DoubleCheck.provider(new SwitchingProvider<SecurePreferencesManager>(singletonCImpl, 17));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 18));
      this.provideBiometricAuthManagerProvider = DoubleCheck.provider(new SwitchingProvider<BiometricAuthManager>(singletonCImpl, 19));
      this.provideSignalCollectorProvider = DoubleCheck.provider(new SwitchingProvider<SignalCollector>(singletonCImpl, 20));
      this.malwareScannerProvider = DoubleCheck.provider(new SwitchingProvider<MalwareScanner>(singletonCImpl, 21));
      this.provideUserRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserRepository>(singletonCImpl, 22));
      this.provideIncidentRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<IncidentRepository>(singletonCImpl, 24));
      this.provideTimelineBuilderProvider = DoubleCheck.provider(new SwitchingProvider<TimelineBuilder>(singletonCImpl, 23));
      this.securityAlertManagerProvider = DoubleCheck.provider(new SwitchingProvider<SecurityAlertManager>(singletonCImpl, 25));
    }

    @Override
    public void injectSentinelGuardApp(SentinelGuardApp sentinelGuardApp) {
      injectSentinelGuardApp2(sentinelGuardApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private SentinelGuardApp injectSentinelGuardApp2(SentinelGuardApp instance) {
      SentinelGuardApp_MembersInjector.injectEmailCredentialInitializer(instance, emailCredentialInitializerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sentinelguard.email.EmailCredentialInitializer 
          return (T) new EmailCredentialInitializer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.emailServiceProvider.get());

          case 1: // com.sentinelguard.email.EmailService 
          return (T) new EmailService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.sentinelguard.security.risk.RiskScoringEngine 
          return (T) new RiskScoringEngine(singletonCImpl.provideRiskScoreRepositoryProvider.get(), singletonCImpl.provideSecuritySignalRepositoryProvider.get(), singletonCImpl.provideBaselineEngineProvider.get(), singletonCImpl.behavioralAnomalyDao(), singletonCImpl.appUsagePatternAnalyzerProvider.get(), singletonCImpl.locationClusterManagerProvider.get(), singletonCImpl.networkBehaviorTrackerProvider.get(), singletonCImpl.unlockPatternAnalyzerProvider.get());

          case 3: // com.sentinelguard.domain.repository.RiskScoreRepository 
          return (T) DatabaseModule_ProvideRiskScoreRepositoryFactory.provideRiskScoreRepository(singletonCImpl.riskScoreDao());

          case 4: // com.sentinelguard.data.database.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDatabasePassphraseManagerProvider.get());

          case 5: // com.sentinelguard.data.database.DatabasePassphraseManager 
          return (T) DatabaseModule_ProvideDatabasePassphraseManagerFactory.provideDatabasePassphraseManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.sentinelguard.domain.repository.SecuritySignalRepository 
          return (T) DatabaseModule_ProvideSecuritySignalRepositoryFactory.provideSecuritySignalRepository(singletonCImpl.securitySignalDao());

          case 7: // com.sentinelguard.security.baseline.BaselineEngine 
          return (T) SecurityModule_ProvideBaselineEngineFactory.provideBaselineEngine(singletonCImpl.provideBaselineRepositoryProvider.get(), singletonCImpl.provideSecuritySignalRepositoryProvider.get(), singletonCImpl.provideSecurePreferencesProvider.get());

          case 8: // com.sentinelguard.domain.repository.BaselineRepository 
          return (T) DatabaseModule_ProvideBaselineRepositoryFactory.provideBaselineRepository(singletonCImpl.behavioralBaselineDao());

          case 9: // com.sentinelguard.data.local.preferences.SecurePreferences 
          return (T) SecurityModule_ProvideSecurePreferencesFactory.provideSecurePreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.sentinelguard.security.baseline.AppUsagePatternAnalyzer 
          return (T) new AppUsagePatternAnalyzer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.appUsageTrackerProvider.get(), singletonCImpl.appUsagePatternDao(), singletonCImpl.behavioralAnomalyDao());

          case 11: // com.sentinelguard.security.collector.AppUsageTracker 
          return (T) new AppUsageTracker(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.permissionManagerProvider.get());

          case 12: // com.sentinelguard.permission.PermissionManager 
          return (T) new PermissionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 13: // com.sentinelguard.security.baseline.LocationClusterManager 
          return (T) new LocationClusterManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.locationClusterDao(), singletonCImpl.behavioralAnomalyDao(), singletonCImpl.permissionManagerProvider.get(), singletonCImpl.provideFusedLocationClientProvider.get());

          case 14: // com.google.android.gms.location.FusedLocationProviderClient 
          return (T) SecurityModule_ProvideFusedLocationClientFactory.provideFusedLocationClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // com.sentinelguard.security.collector.NetworkBehaviorTracker 
          return (T) new NetworkBehaviorTracker(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.knownNetworkDao(), singletonCImpl.behavioralAnomalyDao());

          case 16: // com.sentinelguard.security.baseline.UnlockPatternAnalyzer 
          return (T) new UnlockPatternAnalyzer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.unlockPatternDao(), singletonCImpl.behavioralAnomalyDao());

          case 17: // com.sentinelguard.data.preferences.SecurePreferencesManager 
          return (T) AuthModule_ProvideSecurePreferencesManagerFactory.provideSecurePreferencesManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 18: // com.sentinelguard.auth.AuthRepository 
          return (T) AuthModule_ProvideAuthRepositoryFactory.provideAuthRepository(singletonCImpl.userDao(), singletonCImpl.provideSecurePreferencesManagerProvider.get());

          case 19: // com.sentinelguard.auth.BiometricAuthManager 
          return (T) AuthModule_ProvideBiometricAuthManagerFactory.provideBiometricAuthManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.sentinelguard.security.collector.SignalCollector 
          return (T) SecurityModule_ProvideSignalCollectorFactory.provideSignalCollector(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.securitySignalDao(), singletonCImpl.provideSecurePreferencesManagerProvider.get(), singletonCImpl.provideFusedLocationClientProvider.get());

          case 21: // com.sentinelguard.scanner.MalwareScanner 
          return (T) new MalwareScanner(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 22: // com.sentinelguard.domain.repository.UserRepository 
          return (T) DatabaseModule_ProvideUserRepositoryFactory.provideUserRepository(singletonCImpl.userDao(), singletonCImpl.provideSecurePreferencesProvider.get());

          case 23: // com.sentinelguard.incident.TimelineBuilder 
          return (T) AlertModule_ProvideTimelineBuilderFactory.provideTimelineBuilder(singletonCImpl.provideSecuritySignalRepositoryProvider.get(), singletonCImpl.provideIncidentRepositoryProvider.get());

          case 24: // com.sentinelguard.domain.repository.IncidentRepository 
          return (T) DatabaseModule_ProvideIncidentRepositoryFactory.provideIncidentRepository(singletonCImpl.incidentDao());

          case 25: // com.sentinelguard.security.alert.SecurityAlertManager 
          return (T) new SecurityAlertManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.emailServiceProvider.get(), singletonCImpl.provideSecurePreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
