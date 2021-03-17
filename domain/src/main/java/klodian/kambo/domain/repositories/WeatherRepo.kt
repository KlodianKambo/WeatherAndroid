package klodian.kambo.domain.repositories

import arrow.core.Either
import klodian.kambo.domain.model.CompleteWeatherInfo
import klodian.kambo.domain.model.SafeRequestError
import klodian.kambo.domain.model.TemperatureMeasurementUnit
import java.util.*

interface WeatherRepo {
    suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureMeasurementUnit
    ): Either<SafeRequestError, CompleteWeatherInfo>
}