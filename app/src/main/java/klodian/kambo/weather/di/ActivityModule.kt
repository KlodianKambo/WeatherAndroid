package klodian.kambo.weather.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import klodian.kambo.weather.BaseActivity
import klodian.kambo.weather.MainActivity

@Module(includes = [ViewModelBinderModule::class])
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesBaseActivityInjector(): BaseActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesMainActivityInjector(): MainActivity
}