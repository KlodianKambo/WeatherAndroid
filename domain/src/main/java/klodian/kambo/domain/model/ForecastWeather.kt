package klodian.kambo.domain.model

data class ForecastWeather(
    val city: String,
    val country: String,
    val completeWeatherInfoList: List<CompleteWeatherInfo>
)