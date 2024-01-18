package klodian.kambo.data.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import klodian.kambo.data.ApiClientConfiguration
import klodian.kambo.data.ApiClientConfigurationImpl
import klodian.kambo.data.BuildIconPath
import klodian.kambo.data.api.WeatherApi
import klodian.kambo.data.repositories.LocationDataSourceImpl
import klodian.kambo.data.repositories.WeatherRemoteDataStoreImpl
import klodian.kambo.domain.repositories.LocationDataSource
import klodian.kambo.domain.repositories.WeatherRemoteDataStore
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
    fun providesOkHttpClient(apiClientConfiguration: ApiClientConfiguration): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor { interceptorChain ->
                var original = interceptorChain.request()
                val url = original
                    .url()
                    .newBuilder()
                    .addQueryParameter(apiClientConfiguration.appIdParam, apiClientConfiguration.appIdValue)
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
        apiClientConfiguration: ApiClientConfiguration
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiClientConfiguration.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    @Singleton
    @Provides
    fun providesDataConfiguration(): ApiClientConfiguration {
        return ApiClientConfigurationImpl()
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
        buildIconPath: BuildIconPath,
    ): WeatherRemoteDataStore = WeatherRemoteDataStoreImpl(weatherApi, buildIconPath, coroutineDispatcher)

    @Provides
    internal fun bindsLocationRepository(
        @ApplicationContext context: Context,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): LocationDataSource =
        LocationDataSourceImpl(context, coroutineDispatcher)

}