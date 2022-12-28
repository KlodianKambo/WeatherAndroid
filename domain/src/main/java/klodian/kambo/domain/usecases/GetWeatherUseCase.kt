package klodian.kambo.domain.usecases

import arrow.core.Either
import klodian.kambo.domain.model.ForecastWeather
import klodian.kambo.domain.model.HttpRequestError
import klodian.kambo.domain.model.TemperatureUnit
import klodian.kambo.domain.repositories.WeatherRepo
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val weatherRepo: WeatherRepo) {
    suspend operator fun invoke(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather> {
        return weatherRepo.getWeather(cityName, locale, measurementUnit, zoneId)
    }
}