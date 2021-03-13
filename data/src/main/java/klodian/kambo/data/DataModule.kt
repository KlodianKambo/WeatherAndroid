package klodian.kambo.data

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import klodian.kambo.domain.WeatherRepo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class DataModule {

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

@Module(includes = [DataModule::class])
abstract class RepositoryModule {

    @Binds
    internal abstract fun bindsRepository(imp: WeatherRepoImpl): WeatherRepo
}
