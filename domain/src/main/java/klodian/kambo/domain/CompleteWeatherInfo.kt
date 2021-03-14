package klodian.kambo.domain

data class CompleteWeatherInfo(
    val weather: List<Weather>,
    val temperature: Temperature
)