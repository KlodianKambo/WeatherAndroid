package klodian.kambo.domain.repositories

import arrow.core.Either
import com.kambo.klodian.entities.model.ForecastWeather
import klodian.kambo.domain.model.HttpRequestError
import com.kambo.klodian.entities.model.TemperatureUnit
import java.time.ZoneId
import java.util.*

interface WeatherRepo {
    suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather>
}