package klodian.kambo.data.repositories

import arrow.core.Either
import klodian.kambo.data.GetIconPathUseCase
import klodian.kambo.data.api.WeatherApi
import klodian.kambo.data.model.ForecastResponseDto
import klodian.kambo.data.model.TemperatureDto
import klodian.kambo.data.model.WeatherDto
import klodian.kambo.data.utils.performSafeRequest
import klodian.kambo.domain.model.*
import klodian.kambo.domain.repositories.WeatherRepo
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val getIconPathUseCase: GetIconPathUseCase
) : WeatherRepo {

    companion object {
        private const val TEMPERATURE_UNIT_IMPERIAL = "imperial"
        private const val TEMPERATURE_UNIT_METRIC = "metric"
    }

    override suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureMeasurementUnit
    ): Either<SafeRequestError, ForecastWeather> = coroutineScope {

        val unit = when (measurementUnit) {
            TemperatureMeasurementUnit.Fahrenheit -> TEMPERATURE_UNIT_IMPERIAL
            TemperatureMeasurementUnit.Celsius -> TEMPERATURE_UNIT_METRIC
        }

        performSafeRequest {
            weatherApi.getWeather(
                cityNamePattern = cityName,
                language = locale.language,
                units = unit
            ).toForecastWeather(measurementUnit)
        }
    }

    private fun WeatherDto.toWeather(): Weather {
        return Weather(
            id = id,
            weather = main,
            description = description,
            iconName = getIconPathUseCase(icon)
        )
    }

    private fun ForecastResponseDto.toForecastWeather(measurementUnit: TemperatureMeasurementUnit): ForecastWeather {
        return ForecastWeather(
            city = city.name,
            country = city.country,
            completeWeatherInfoList = list.map { weatherResponse ->
                CompleteWeatherInfo(
                    date = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(weatherResponse.dateLongMillis),
                        ZoneId.systemDefault()
                    ),
                    temperature = weatherResponse.main.toTemperature(measurementUnit),
                    weather = weatherResponse.weather.map { it.toWeather() })
            })
    }

    private fun TemperatureDto.toTemperature(measurementUnit: TemperatureMeasurementUnit): Temperature {
        return Temperature(temp, feelsLike, minTemp, maxTemp, pressure, humidity, measurementUnit)
    }
}