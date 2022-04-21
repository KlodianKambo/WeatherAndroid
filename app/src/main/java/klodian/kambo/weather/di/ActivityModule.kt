package klodian.kambo.weather.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.kambo.klodian.ui.ui.BaseActivity
import com.kambo.klodian.ui.ui.weather.WeatherActivity

@Module(includes = [ViewModelBinderModule::class])
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesBaseActivityInjector(): BaseActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributesMainActivityInjector(): WeatherActivity
}