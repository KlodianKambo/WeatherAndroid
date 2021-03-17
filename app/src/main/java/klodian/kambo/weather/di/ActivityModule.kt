package klodian.kambo.weather.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import klodian.kambo.weather.BaseActivity
import klodian.kambo.weather.ui.weather.WeatherActivity

@Module(includes = [ViewModelBinderModule::class])
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesBaseActivityInjector(): BaseActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesMainActivityInjector(): WeatherActivity
}