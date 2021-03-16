package klodian.kambo.domain

import arrow.core.Either
import java.util.*

interface WeatherRepo {
    suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureMeasurementUnit
    ): Either<SafeRequestError, CompleteWeatherInfo>
}