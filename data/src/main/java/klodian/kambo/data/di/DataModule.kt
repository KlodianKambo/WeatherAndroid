package klodian.kambo.data.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import klodian.kambo.data.DataConfiguration
import klodian.kambo.data.DataConfigurationImpl
import klodian.kambo.data.GetIconPathUseCase
import klodian.kambo.data.api.WeatherApi
import klodian.kambo.data.repositories.LocationRepositoryImpl
import klodian.kambo.data.repositories.WeatherRepositoryImpl
import klodian.kambo.domain.repositories.LocationRepository
import klodian.kambo.domain.repositories.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Singleton
    @Provides
    fun providesOkHttpClient(dataConfiguration: DataConfiguration): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor { interceptorChain ->
                var original = interceptorChain.request()
                val url = original
                    .url()
                    .newBuilder()
                    .addQueryParameter(dataConfiguration.appIdParam, dataConfiguration.appIdValue)
                    .build()

                original = original.newBuilder().url(url).build()
                interceptorChain.proceed(original)
            }
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        dataConfiguration: DataConfiguration
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(dataConfiguration.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    @Singleton
    @Provides
    fun providesDataConfiguration(): DataConfiguration {
        return DataConfigurationImpl()
    }

    @Singleton
    @Provides
    fun providesAccountApi(retrofit: Retrofit): WeatherApi = retrofit.create()
}

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    internal fun bindsRepository(
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
        weatherApi: WeatherApi,
        getIconPathUseCase: GetIconPathUseCase,
    ): WeatherRepository = WeatherRepositoryImpl(weatherApi, getIconPathUseCase, coroutineDispatcher)

    @Provides
    internal fun bindsLocationRepository(
        @ApplicationContext context: Context,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): LocationRepository =
        LocationRepositoryImpl(context, coroutineDispatcher)

}