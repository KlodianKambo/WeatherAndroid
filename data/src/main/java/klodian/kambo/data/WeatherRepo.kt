package klodian.kambo.data

import klodian.kambo.domain.Weather
import klodian.kambo.domain.WeatherRepo
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val getIconPath: GetIconPath
) : WeatherRepo {
    override suspend fun getWeather(cityName: String) = coroutineScope {
        weatherApi.getSongs(cityName).weather.map { it.toWeather() }
    }

    private fun WeatherDto.toWeather(): Weather {
        return Weather(
            id = id,
            weather = main,
            description = description,
            iconName = getIconPath(icon)
        )
    }
}