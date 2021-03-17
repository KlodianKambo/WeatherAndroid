package klodian.kambo.domain.model

data class CompleteWeatherInfo(
    val weather: List<Weather>,
    val temperature: Temperature
)