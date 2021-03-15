package klodian.kambo.data

import arrow.core.Either
import klodian.kambo.domain.*
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val getIconPath: GetIconPath
) : WeatherRepo {

    override suspend fun getWeather(
        cityName: String,
        locale: Locale
    ): Either<SafeRequestError, CompleteWeatherInfo> = coroutineScope {
        performSafeRequest {
            val weatherResponse = weatherApi.getWeather(cityName, locale.language)
            CompleteWeatherInfo(temperature = weatherResponse.main.toTemperature(),
                weather = weatherResponse.weather.map { it.toWeather() })
        }
    }

    private fun WeatherDto.toWeather(): Weather {
        return Weather(
            id = id,
            weather = main,
            description = description,
            iconName = getIconPath(icon)
        )
    }

    private fun TemperatureDto.toTemperature(): Temperature {
        return Temperature(temp, feelsLike, minTemp, maxTemp, pressure, humidity)
    }
}