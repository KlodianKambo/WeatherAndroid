package klodian.kambo.domain.repositories

import arrow.core.Either
import klodian.kambo.domain.model.ForecastWeather
import klodian.kambo.domain.model.SafeRequestError
import klodian.kambo.domain.model.TemperatureMeasurementUnit
import java.time.ZoneId
import java.util.*

interface WeatherRepo {
    suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureMeasurementUnit,
        zoneId: ZoneId
    ): Either<SafeRequestError, ForecastWeather>
}