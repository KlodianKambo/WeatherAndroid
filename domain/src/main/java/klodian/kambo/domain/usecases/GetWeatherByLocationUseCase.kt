package klodian.kambo.domain.usecases

import arrow.core.Either
import com.kambo.klodian.entities.model.ForecastWeather
import com.kambo.klodian.entities.model.TemperatureUnit
import klodian.kambo.domain.model.HttpRequestError
import klodian.kambo.domain.repositories.WeatherRemoteDataStore
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class GetWeatherByLocationUseCase @Inject constructor(private val weatherRemoteDataStore: WeatherRemoteDataStore) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        locale: Locale,
        measurementUnit: TemperatureUnit,
        zoneId: ZoneId
    ): Either<HttpRequestError, ForecastWeather> {
        return weatherRemoteDataStore.getWeatherByLocation(
            latitude,
            longitude,
            locale,
            measurementUnit,
            zoneId
        )
    }
}



