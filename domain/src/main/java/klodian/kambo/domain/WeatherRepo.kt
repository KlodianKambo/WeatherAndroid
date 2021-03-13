package klodian.kambo.domain

interface WeatherRepo {
    suspend fun getWeather(cityName: String): List<Weather>
}