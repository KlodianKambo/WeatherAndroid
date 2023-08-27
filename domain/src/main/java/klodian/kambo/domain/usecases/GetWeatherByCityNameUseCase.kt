package klodian.kambo.domain.usecases

import arrow.core.Either
import com.kambo.klodian.entities.model.ForecastWeather
import klodian.kambo.domain.model.HttpRequestError
import com.kambo.klodian.entities.model.TemperatureUnit
import klodian.kambo.domain.repositories.WeatherRepository
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class GetWeatherByCityNameUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather> {
        return weatherRepository.getWeather(cityName, locale, measurementUnit, zoneId)
    }
}