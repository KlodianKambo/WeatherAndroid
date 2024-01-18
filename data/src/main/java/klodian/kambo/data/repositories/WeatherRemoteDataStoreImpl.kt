package klodian.kambo.data.repositories

import arrow.core.Either
import com.kambo.klodian.entities.model.*
import klodian.kambo.data.BuildIconPath
import klodian.kambo.data.api.WeatherApi
import klodian.kambo.data.model.ForecastResponseDto
import klodian.kambo.data.model.TemperatureDto
import klodian.kambo.data.model.WeatherDto
import klodian.kambo.data.utils.performCatchingRequest
import klodian.kambo.domain.model.HttpRequestError
import klodian.kambo.domain.repositories.WeatherRemoteDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

internal class WeatherRemoteDataStoreImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val buildIconPath: BuildIconPath,
    private val coroutineDispatcher: CoroutineDispatcher
) : WeatherRemoteDataStore {

    companion object {
        private const val TEMPERATURE_UNIT_IMPERIAL = "imperial"
        private const val TEMPERATURE_UNIT_METRIC = "metric"
    }

    override suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather> = withContext(coroutineDispatcher) {

        val unit = when (measurementUnit) {
            TemperatureUnit.Fahrenheit -> TEMPERATURE_UNIT_IMPERIAL
            TemperatureUnit.Celsius -> TEMPERATURE_UNIT_METRIC
        }

        supervisorScope {
            performCatchingRequest {
                weatherApi.getWeather(
                    cityNamePattern = cityName,
                    language = locale.language,
                    units = unit
                ).toForecastWeather(measurementUnit, zoneId)
            }
        }
    }

    override suspend fun getWeatherByLocation(
        latitude: Double,
        longitude: Double,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather> = withContext(coroutineDispatcher) {

        val unit = when (measurementUnit) {
            TemperatureUnit.Fahrenheit -> TEMPERATURE_UNIT_IMPERIAL
            TemperatureUnit.Celsius -> TEMPERATURE_UNIT_METRIC
        }

        supervisorScope {
            performCatchingRequest {
                weatherApi.getWeatherByLocation(
                    latitude = latitude,
                    longitude = longitude,
                    language = locale.language,
                    units = unit
                ).toForecastWeather(measurementUnit, zoneId)
            }
        }
    }


    /***********************************************************************************************
     * Private fun
     **********************************************************************************************/
    private fun WeatherDto.toWeather(): Weather {
        return Weather(
            id = id,
            weather = main,
            description = description,
            iconName = buildIconPath(icon)
        )
    }

    private fun ForecastResponseDto.toForecastWeather(
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): ForecastWeather {
        return ForecastWeather(
            city = city.name,
            country = city.country,
            completeWeatherInfoList = list.map { weatherResponse ->
                CompleteWeatherInfo(
                    date = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(weatherResponse.dateLongMillis + city.timezone),
                        zoneId
                    ),
                    temperature = weatherResponse.main.toTemperature(measurementUnit),
                    weather = weatherResponse.weather.map { it.toWeather() })
            })
    }

    private fun TemperatureDto.toTemperature(measurementUnit: TemperatureUnit): Temperature {
        return Temperature(temp, feelsLike, minTemp, maxTemp, pressure, humidity, measurementUnit)
    }
}