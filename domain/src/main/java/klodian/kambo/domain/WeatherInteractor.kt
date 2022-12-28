package klodian.kambo.domain

import arrow.core.Either
import arrow.core.flatMap
import klodian.kambo.domain.model.ForecastWeather
import klodian.kambo.domain.model.TemperatureUnit
import klodian.kambo.domain.model.WeatherInteractorError
import klodian.kambo.domain.repositories.WeatherRepo
import klodian.kambo.domain.usecases.GetValidSearchPatternUseCase
import java.time.ZoneId
import java.util.*
import javax.inject.Inject


interface GetWeather {
    suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<WeatherInteractorError, ForecastWeather>
}


class WeatherInteractor @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val getValidSearchPatternUseCase: GetValidSearchPatternUseCase
) : GetWeather {

    override suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<WeatherInteractorError, ForecastWeather> {

        return getValidSearchPatternUseCase(cityName).flatMap {
            weatherRepo.getWeather(it, locale, measurementUnit, zoneId).fold(
                ifLeft = { Either.left(WeatherInteractorError.HttpError(it)) },
                ifRight = { Either.right(it) }
            )
        }
    }

}