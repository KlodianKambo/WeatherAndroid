package klodian.kambo.weather

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import klodian.kambo.weather.di.DaggerAppComponent
import timber.log.Timber

class WeatherApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}