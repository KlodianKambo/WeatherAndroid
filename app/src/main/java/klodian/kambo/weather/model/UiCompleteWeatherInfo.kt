package klodian.kambo.weather.model

data class UiCompleteWeatherInfo(
    val weather: List<UiWeather>,
    val temperature: UiTemperature
)