package klodian.kambo.domain

import java.util.*

interface WeatherRepo {
    suspend fun getWeather(cityName: String, locale: Locale): List<Weather>
}