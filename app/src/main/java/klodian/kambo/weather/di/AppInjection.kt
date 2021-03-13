package klodian.kambo.weather.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import klodian.kambo.data.DataModule
import klodian.kambo.data.RepositoryModule
import klodian.kambo.weather.WeatherApplication
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        DataModule::class,
        ActivityModule::class,
        RepositoryModule::class
    ]
)

interface AppComponent : AndroidInjector<WeatherApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: WeatherApplication): Builder

        fun build(): AppComponent
    }
}

@Module
class AppModule {
    @Provides
    fun providesContext(app: WeatherApplication): Context = app

    @Provides
    fun providesApplication(app: WeatherApplication): Application = app
}